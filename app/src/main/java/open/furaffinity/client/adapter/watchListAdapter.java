package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.utilities.webClient;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ViewHolder> {
    private static final String TAG = watchListAdapter.class.getName();

    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public watchListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_user_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(mDataSet.get(position).get("userIcon")).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(holder.userIcon);
        holder.userName.setText(mDataSet.get(position).get("userName"));

        if (mDataSet.get(position).get("userLink") != null) {
            holder.userIcon.setOnClickListener(v -> ((mainActivity) context).setUserPath(mDataSet.get(position).get("userLink")));
            holder.userName.setOnClickListener(v -> ((mainActivity) context).setUserPath(mDataSet.get(position).get("userLink")));
        }

        holder.deleteButton.setOnClickListener(v -> {
            try {
                new AsyncTask<webClient, Void, Void>() {
                    @Override
                    protected Void doInBackground(webClient... webClients) {
                        webClients[0].sendGetRequest(webClient.getBaseUrl() + mDataSet.get(position).get("userRemoveLink"));
                        return null;
                    }
                }.execute(new webClient(context)).get();

                mDataSet.remove(position);
                notifyDataSetChanged();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Could not unWatch user: ", e);
            }
        });
    }

    @Override
    public int getItemCount() {
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
