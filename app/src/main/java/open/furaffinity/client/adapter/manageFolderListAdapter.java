package open.furaffinity.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class manageFolderListAdapter extends RecyclerView.Adapter<manageFolderListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private manageFolderListAdapterListener listener;

    public manageFolderListAdapter(List<HashMap<String, String>> mDataSetIn) {
        mDataSet = mDataSetIn;
    }

    public void setListener(manageFolderListAdapterListener manageFolderListAdapterListener) {
        listener = manageFolderListAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView).load(mDataSet.get(position).get("iconLink")).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(holder.itemIcon);
        holder.itemName.setText(mDataSet.get(position).get("name"));

        holder.upButton.setOnClickListener(v -> {
            if (mDataSet.get(position).containsKey("upgroup_id")) {
                listener.upButton(mDataSet.get(position).get("upaction"), mDataSet.get(position).get("upkey"), mDataSet.get(position).get("updirection"), mDataSet.get(position).get("upgroup_id"), "group_id");
            } else if (mDataSet.get(position).containsKey("upfolder_id")) {
                listener.upButton(mDataSet.get(position).get("upaction"), mDataSet.get(position).get("upkey"), mDataSet.get(position).get("updirection"), mDataSet.get(position).get("upfolder_id"), "folder_id");
            }
        });

        holder.downButton.setOnClickListener(v -> {
            if (mDataSet.get(position).containsKey("downgroup_id")) {
                listener.downButton(mDataSet.get(position).get("downaction"), mDataSet.get(position).get("downkey"), mDataSet.get(position).get("downdirection"), mDataSet.get(position).get("downgroup_id"), "group_id");
            } else if (mDataSet.get(position).containsKey("downfolder_id")) {
                listener.downButton(mDataSet.get(position).get("downaction"), mDataSet.get(position).get("downkey"), mDataSet.get(position).get("downdirection"), mDataSet.get(position).get("downfolder_id"), "folder_id");
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (mDataSet.get(position).containsKey("deletegroup_id")) {
                listener.deleteButton(mDataSet.get(position).get("deleteaction"), mDataSet.get(position).get("deletekey"), mDataSet.get(position).get("deletegroup_id"), "group_id");
            } else if (mDataSet.get(position).containsKey("deletefolder_id")) {
                listener.deleteButton(mDataSet.get(position).get("deleteaction"), mDataSet.get(position).get("deletekey"), mDataSet.get(position).get("deletefolder_id"), "folder_id");
            }
        });

        if (mDataSet.get(position).containsKey("editfolder_id")) {
            holder.editButton.setVisibility(View.VISIBLE);
        } else {
            holder.editButton.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> {
            if (mDataSet.get(position).containsKey("editfolder_id")) {
                listener.editButton(mDataSet.get(position).get("editaction"), mDataSet.get(position).get("editfolder_id"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface manageFolderListAdapterListener {
        void upButton(String postURL, String key, String position, String id, String idName);

        void downButton(String postURL, String key, String position, String id, String idName);

        void deleteButton(String postURL, String key, String id, String idName);

        void editButton(String postURL, String id);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemIcon;
        private final TextView itemName;
        private final Button upButton;
        private final Button downButton;
        private final Button deleteButton;
        private final Button editButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemIcon = itemView.findViewById(R.id.itemIcon);
            itemName = itemView.findViewById(R.id.itemName);
            upButton = itemView.findViewById(R.id.upButton);
            downButton = itemView.findViewById(R.id.downButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}
