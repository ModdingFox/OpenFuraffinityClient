package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.utilities.webClient;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ViewHolder> {
    private static final String TAG = watchListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public watchListAdapter(ArrayList<String> mDataSetIn, Context context) {
        mDataSet = new ArrayList<>();
        this.context = context;

        for (String currentItem : mDataSetIn) {
            HashMap<String, String> newDataItem = new HashMap<>();
            newDataItem.put("item", currentItem);
            mDataSet.add(newDataItem);
        }
    }

    public watchListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linearLayout;
        private final ImageView userIcon;
        private final TextView userName;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            userIcon = itemView.findViewById(R.id.userIcon);
            userName = itemView.findViewById(R.id.userName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_user_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(mDataSet.get(position).get("userIcon")).into(holder.userIcon);
        holder.userName.setText(mDataSet.get(position).get("userName"));

        if (mDataSet.get(position).get("userLink") != null) {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((mainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                }
            });
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + mDataSet.get(position).get("userRemoveLink"));
                            return null;
                        }
                    }.execute(new webClient(context)).get();

                    mDataSet.remove(position);
                    notifyDataSetChanged();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not unWatch user: ", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
