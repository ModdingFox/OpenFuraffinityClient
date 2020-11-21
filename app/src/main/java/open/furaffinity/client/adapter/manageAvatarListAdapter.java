package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class manageAvatarListAdapter extends RecyclerView.Adapter<manageAvatarListAdapter.ViewHolder> {
    private List<HashMap<String, String>> mDataSet;

    private Context context;

    public interface manageAvatarListAdapterListener {
        public void onSet(String url);

        public void onDelete(String url);
    }

    manageAvatarListAdapterListener listener;

    public void setListener(manageAvatarListAdapterListener manageAvatarListAdapterListener) {
        listener = manageAvatarListAdapterListener;
    }

    public manageAvatarListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_x2_button_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(mDataSet.get(position).get("imgUrl")).into(holder.imageView);

        holder.setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSet(mDataSet.get(position).get("setUrl"));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(mDataSet.get(position).get("deleteUrl"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
