package open.furaffinity.client.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.fragmentDrawers.Journal;
import open.furaffinity.client.fragmentDrawers.User;
import open.furaffinity.client.fragmentDrawers.View;
import open.furaffinity.client.fragmentTabs.MsgPmsMessage;

public class StringListAdapter extends RecyclerView.Adapter<StringListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public StringListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.text_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setText(mDataSet.get(position).get("item"));

        if (mDataSet.get(position).get("path") != null
            && mDataSet.get(position).get("class") != null) {
            holder.item.setOnClickListener(view -> {
                if (mDataSet.get(position).get("class").equals(Journal.class.getName())) {
                    ((MainActivity) context).setJournalPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class")
                    .equals(MsgPmsMessage.class.getName())) {
                    ((MainActivity) context).setMsgPmsPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class").equals(User.class.getName())) {
                    ((MainActivity) context).setUserPath(mDataSet.get(position).get("path"));
                }
                else if (mDataSet.get(position).get("class").equals(View.class.getName())) {
                    ((MainActivity) context).setViewPath(mDataSet.get(position).get("path"));
                }
            });
        }
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item;

        ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.text_item);
        }
    }
}
