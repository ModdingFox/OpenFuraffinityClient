package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;

public class msgPmsListAdapter extends RecyclerView.Adapter<msgPmsListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;
    private List<String> checkedItems;

    public msgPmsListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
        checkedItems = new ArrayList<>();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.msgpms_item, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDataSet.get(position).containsKey("messageSender")) {
            holder.userName.setText(mDataSet.get(position).get("messageSender"));

            if (mDataSet.get(position).containsKey("messageSenderLink")) {
                holder.userName.setOnClickListener(v -> ((mainActivity) context).setUserPath(
                    mDataSet.get(position).get("messageSenderLink")));
            }
        }
        else {
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
            holder.messageText.setOnClickListener(v -> ((mainActivity) context).setMsgPmsPath(
                mDataSet.get(position).get("messageLink")));
        }

        holder.checkBox.setChecked(checkedItems.contains(mDataSet.get(position).get("messageid")));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItems.add(mDataSet.get(position).get("messageid"));
            }
            else {
                checkedItems.remove(mDataSet.get(position).get("messageid"));
            }
        });
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public void clearChecked() {
        checkedItems = new ArrayList<>();
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView messageText;
        private final CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            messageText = itemView.findViewById(R.id.messageText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}