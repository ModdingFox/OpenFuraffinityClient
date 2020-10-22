package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;

public class imageListAdapter extends RecyclerView.Adapter<imageListAdapter.ViewHolder> {
    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public imageListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImage;
        private final TextView postInfo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imageListCardViewPostImage);
            postInfo = itemView.findViewById(R.id.imageListCardPostInfo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagelist_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load("https:" + mDataSet.get(position).get("imgUrl")).into(holder.postImage);
        holder.postInfo.setText(String.format("%s by %s", mDataSet.get(position).get("postTitle"), mDataSet.get(position).get("postUserName")));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) context).setViewPath(mDataSet.get(position).get("postPath"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
