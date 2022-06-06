package open.furaffinity.client.adapter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.sqlite.SearchContract.searchItemEntry;
import open.furaffinity.client.sqlite.SearchDBHelper;
import open.furaffinity.client.utilities.NotificationItem;

public class SavedSearchListAdapter
    extends RecyclerView.Adapter<SavedSearchListAdapter.ViewHolder> {
    private final List<NotificationItem> mDataSet;
    private final Context context;

    public SavedSearchListAdapter(List<NotificationItem> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.savedsearch_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTextView.setText(mDataSet.get(position).getName());
        holder.notificationSwitch.setChecked(mDataSet.get(position).getState());

        holder.nameTextView.setOnClickListener(view -> {
            ((MainActivity) context).setSearchSelected(
                Integer.toString(mDataSet.get(position).getRowId()));
        });

        holder.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final SearchDBHelper dbHelper = new SearchDBHelper(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            final ContentValues values = new ContentValues();
            if (isChecked) {
                values.put(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE, 1);
            }
            else {
                values.put(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE, 0);
            }

            final String selection = "rowid = ?";
            final String[] selectionArgs = {Integer.toString(mDataSet.get(position).getRowId())};

            db.update(searchItemEntry.TABLE_NAME, values, selection, selectionArgs);
            db.close();

            mDataSet.get(position).setState(isChecked);
        });

        holder.deleteButton.setOnClickListener(view -> {
            final SearchDBHelper dbHelper = new SearchDBHelper(context);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            final String selection = "rowid = ?";
            final String[] selectionArgs = {Integer.toString(mDataSet.get(position).getRowId())};
            db.delete(searchItemEntry.TABLE_NAME, selection, selectionArgs);
            db.close();

            // This is cheap but it works
            final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = 0;
            holder.itemView.setLayoutParams(layoutParams);

            holder.itemView.setVisibility(View.GONE);
        });
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final Switch notificationSwitch;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            notificationSwitch = itemView.findViewById(R.id.notificationSwitch);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
