package open.furaffinity.client.adapter;

import android.content.Context;
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
import open.furaffinity.client.fragmentDrawers.journal;
import open.furaffinity.client.fragmentDrawers.user;
import open.furaffinity.client.fragmentDrawers.view;
import open.furaffinity.client.fragmentTabs.msgPmsMessage;

public class stringListAdapter extends RecyclerView.Adapter<stringListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public stringListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setText(mDataSet.get(position).get("item"));

        if (mDataSet.get(position).get("path") != null &&
            mDataSet.get(position).get("class") != null) {
            holder.item.setOnClickListener(v -> {
                if (mDataSet.get(position).get("class").equals(journal.class.getName())) {
                    ((mainActivity) context).setJournalPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class")
                    .equals(msgPmsMessage.class.getName())) {
                    ((mainActivity) context).setMsgPmsPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class").equals(user.class.getName())) {
                    ((mainActivity) context).setUserPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class").equals(view.class.getName())) {
                    ((mainActivity) context).setViewPath(mDataSet.get(position).get("path"));
                }
            });
        }
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.text_item);
        }
    }
}
