package open.furaffinity.client.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.submitPages.SubmitGetRequest;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public WatchListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.checkbox_user_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(mDataSet.get(position).get("userIcon"))
            .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
            .into(holder.userIcon);
        holder.userName.setText(mDataSet.get(position).get("userName"));

        if (mDataSet.get(position).get("userLink") != null) {
            holder.userIcon.setOnClickListener(
                view -> {
                    ((MainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                });
            holder.userName.setOnClickListener(
                view -> {
                    ((MainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                });
        }

        holder.deleteButton.setOnClickListener(
            view -> {
                new SubmitGetRequest(context, new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            mDataSet.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(
                                context,
                                    "Successfully removed watch",
                                    Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(
                                context,
                                    "Failed to remove watch",
                                    Toast.LENGTH_SHORT)
                                .show();
                        }
                    }, mDataSet.get(position).get("userRemoveLink")).execute();
            });
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userIcon;
        private final TextView userName;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userIcon = itemView.findViewById(R.id.userIcon);
            userName = itemView.findViewById(R.id.userName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
