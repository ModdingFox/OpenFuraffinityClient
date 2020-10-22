package open.furaffinity.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.utilities.messageIds;

public class msgPmsListAdapter extends RecyclerView.Adapter<msgPmsListAdapter.ViewHolder> {
    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public msgPmsListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView messageText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msgpms_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDataSet.get(position).containsKey("messageSender")) {
            holder.userName.setText(mDataSet.get(position).get("messageSender"));

            if (mDataSet.get(position).containsKey("messageSenderLink")) {
                holder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((mainActivity)context).setUserPath(mDataSet.get(position).get("messageSenderLink"));
                    }
                });
            }
        } else {
            holder.userName.setVisibility(View.GONE);
        }

        String messageText = " ";

        if (mDataSet.get(position).containsKey("messageSubject")) {
            messageText += mDataSet.get(position).get("messageSubject");
        }
        if (mDataSet.get(position).containsKey("messageSendDate")) {
            messageText += " " + mDataSet.get(position).get("messageSendDate");
        }

        holder.messageText.setText(messageText);

        if (mDataSet.get(position).containsKey("messageLink")) {
            holder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((mainActivity)context).setMsgPmsPath(mDataSet.get(position).get("messageLink"));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}