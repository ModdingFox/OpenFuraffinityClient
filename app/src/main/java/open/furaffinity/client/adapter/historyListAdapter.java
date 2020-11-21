package open.furaffinity.client.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragments.journal;
import open.furaffinity.client.fragments.msgPmsMessage;
import open.furaffinity.client.fragments.user;
import open.furaffinity.client.fragments.view;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;

public class historyListAdapter extends RecyclerView.Adapter<historyListAdapter.ViewHolder> {
    private static final String TAG = historyListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public historyListAdapter(ArrayList<String> mDataSetIn, Context context) {
        mDataSet = new ArrayList<>();
        this.context = context;

        for (String currentItem : mDataSetIn) {
            HashMap<String, String> newDataItem = new HashMap<>();
            newDataItem.put("item", currentItem);
            mDataSet.add(newDataItem);
        }
    }

    public historyListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.text_item);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_and_button_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setText(mDataSet.get(position).get("item"));

        if (mDataSet.get(position).get("path") != null && mDataSet.get(position).get("class") != null) {
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataSet.get(position).get("class").equals(journal.class.getName())) {
                        ((mainActivity) context).setJournalPath(mDataSet.get(position).get("path"));
                    } else if (mDataSet.get(position).get("class").equals(msgPmsMessage.class.getName())) {
                        ((mainActivity) context).setMsgPmsPath(mDataSet.get(position).get("path"));
                    } else if (mDataSet.get(position).get("class").equals(user.class.getName())) {
                        ((mainActivity) context).setUserPath(mDataSet.get(position).get("path"));
                    } else if (mDataSet.get(position).get("class").equals(view.class.getName())) {
                        ((mainActivity) context).setViewPath(mDataSet.get(position).get("path"));
                    }
                }
            });
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDBHelper dbHelper = new historyDBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                //Delete previous versions from history
                String selection = historyContract.historyItemEntry.COLUMN_NAME_URL + " = ?";
                String[] selectionArgs = {mDataSet.get(position).get("path")};

                if (mDataSet.get(position).get("class").equals(journal.class.getName())) {
                    db.delete(historyContract.historyItemEntry.TABLE_NAME_JOURNAL, selection, selectionArgs);
                } else if (mDataSet.get(position).get("class").equals(user.class.getName())) {
                    db.delete(historyContract.historyItemEntry.TABLE_NAME_USER, selection, selectionArgs);
                } else if (mDataSet.get(position).get("class").equals(view.class.getName())) {
                    db.delete(historyContract.historyItemEntry.TABLE_NAME_VIEW, selection, selectionArgs);
                }

                db.close();

                mDataSet.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
