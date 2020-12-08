package open.furaffinity.client.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.listener.OnSwipeTouchListener;

public class manageImageListAdapter extends RecyclerView.Adapter<manageImageListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;
    private final boolean showPostInfo;
    manageImageListAdapterListener listener;
    private List<String> checkedItems;

    public manageImageListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.settingsFile), Context.MODE_PRIVATE);
        showPostInfo = sharedPref.getBoolean(context.getString(R.string.imageListInfo), settings.imageListInfoDefault);

        checkedItems = new ArrayList<>();
    }

    public void setListener(manageImageListAdapterListener manageImageListAdapterListener) {
        listener = manageImageListAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_imagelist_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load("https:" + mDataSet.get(position).get("imgUrl")).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(holder.postImage);

        if (mDataSet.get(position).containsKey("postTitle")) {
            holder.postName.setText(String.format("%s", mDataSet.get(position).get("postTitle")));
        } else {
            holder.postName.setText(String.format("%s", "Missing Title/Deleted"));
        }

        if (mDataSet.get(position).containsKey("postUserName")) {
            holder.postUser.setText(String.format("By: %s", mDataSet.get(position).get("postUserName")));
        } else {
            holder.postUser.setText("");//Setting this as empty as the submissions page does not return the user name in the listing
        }

        holder.postRating.setText(mDataSet.get(position).get("postRatingCode"));

        holder.postName.setChecked(checkedItems.contains(mDataSet.get(position).get("postId")));

        if (!showPostInfo) {
            holder.imageListPostInfo.setVisibility(View.GONE);
        }


        holder.itemView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                if (listener != null) {
                    listener.onSwipeRight(mDataSet.get(position).get("postId"));
                }
            }

            @Override
            public void onSwipeLeft() {
                if (listener != null) {
                    listener.onSwipeLeft(mDataSet.get(position).get("postId"));
                }
            }

            @Override
            public void onClick() {
                if (mDataSet.get(position).containsKey("postPath")) {
                    ((mainActivity) context).setViewPath(mDataSet.get(position).get("postPath"));
                }
            }
        });

        holder.postName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItems.add(mDataSet.get(position).get("postId"));
            } else {
                checkedItems.remove(mDataSet.get(position).get("postId"));
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

    public interface manageImageListAdapterListener {
        void onSwipeRight(String postId);

        void onSwipeLeft(String postId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
}
