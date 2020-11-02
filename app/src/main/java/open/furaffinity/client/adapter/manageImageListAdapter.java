package open.furaffinity.client.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragments.settings;

public class manageImageListAdapter extends RecyclerView.Adapter<manageImageListAdapter.ViewHolder> {
    private List<HashMap<String, String>> mDataSet;
    private List<String> checkedItems;

    private Context context;
    private boolean showPostInfo = true;

    public manageImageListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);
        showPostInfo = sharedPref.getBoolean(context.getString(R.string.imageListInfo), settings.imageListInfoDefault);

        checkedItems = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImage;
        private final ConstraintLayout imageListPostInfo;
        private final CheckBox postName;
        private final TextView postUser;
        private final TextView postRating;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imageListCardViewPostImage);
            imageListPostInfo = itemView.findViewById(R.id.imageListPostInfo);
            postName = itemView.findViewById(R.id.imageListCardPostName);
            postUser = itemView.findViewById(R.id.imageListCardPostUser);
            postRating = itemView.findViewById(R.id.imageListCardPostRating);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_imagelist_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load("https:" + mDataSet.get(position).get("imgUrl")).into(holder.postImage);
        holder.postName.setText(String.format("%s", mDataSet.get(position).get("postTitle")));
        holder.postUser.setText("");//Setting this as empty as the submissions page does not return the user name in the listing
        holder.postRating.setText(mDataSet.get(position).get("postRatingCode"));

        if(checkedItems.contains(mDataSet.get(position).get("postId"))) {
            holder.postName.setChecked(true);
        }

        if(!showPostInfo) {
            holder.imageListPostInfo.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) context).setViewPath(mDataSet.get(position).get("postPath"));
            }
        });

        holder.postName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedItems.add(mDataSet.get(position).get("postId"));
                } else {
                    checkedItems.remove(mDataSet.get(position).get("postId"));
                }
            }
        });
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public void clearChecked() {
        checkedItems = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}