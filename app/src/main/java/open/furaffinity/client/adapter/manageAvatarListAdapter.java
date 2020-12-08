package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class manageAvatarListAdapter extends RecyclerView.Adapter<manageAvatarListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;

    private final Context context;
    manageAvatarListAdapterListener listener;

    public manageAvatarListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public void setListener(manageAvatarListAdapterListener manageAvatarListAdapterListener) {
        listener = manageAvatarListAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_x2_button_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(mDataSet.get(position).get("imgUrl")).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(holder.imageView);

        holder.setButton.setOnClickListener(v -> listener.onSet(mDataSet.get(position).get("setUrl")));

        holder.deleteButton.setOnClickListener(v -> listener.onDelete(mDataSet.get(position).get("deleteUrl")));

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface manageAvatarListAdapterListener {
        void onSet(String url);

        void onDelete(String url);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final Button setButton;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            setButton = itemView.findViewById(R.id.setButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
