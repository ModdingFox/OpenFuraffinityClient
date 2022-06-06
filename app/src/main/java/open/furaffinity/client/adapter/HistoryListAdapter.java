package open.furaffinity.client.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.fragmentDrawers.Journal;
import open.furaffinity.client.fragmentDrawers.User;
import open.furaffinity.client.fragmentDrawers.View;
import open.furaffinity.client.fragmentTabs.MsgPmsMessage;
import open.furaffinity.client.sqlite.HistoryContract;
import open.furaffinity.client.sqlite.HistoryDBHelper;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public HistoryListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.text_and_button_item,
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

        holder.deleteButton.setOnClickListener(view -> {
            final HistoryDBHelper dbHelper = new HistoryDBHelper(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            final String selection = HistoryContract.historyItemEntry.COLUMN_NAME_URL + " = ?";
            final String[] selectionArgs = {mDataSet.get(position).get("path")};

            if (mDataSet.get(position).get("class").equals(Journal.class.getName())) {
                db.delete(HistoryContract.historyItemEntry.TABLE_NAME_JOURNAL, selection,
                    selectionArgs);
            }
            else if (mDataSet.get(position).get("class").equals(User.class.getName())) {
                db.delete(HistoryContract.historyItemEntry.TABLE_NAME_USER, selection,
                    selectionArgs);
            }
            else if (mDataSet.get(position).get("class").equals(View.class.getName())) {
                db.delete(HistoryContract.historyItemEntry.TABLE_NAME_VIEW, selection,
                    selectionArgs);
            }

            db.close();

            mDataSet.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item;
        private final Button deleteButton;

        ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.text_item);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
