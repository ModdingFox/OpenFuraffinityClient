package open.furaffinity.client.adapter;

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
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.sqlite.searchContract.searchItemEntry;
import open.furaffinity.client.sqlite.searchDBHelper;
import open.furaffinity.client.utilities.notificationItem;

public class savedSearchListAdapter
    extends RecyclerView.Adapter<savedSearchListAdapter.ViewHolder> {
    private final List<notificationItem> mDataSet;
    private final Context context;

    public savedSearchListAdapter(List<notificationItem> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.savedsearch_item, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTextView.setText(mDataSet.get(position).getName());
        holder.notificationSwitch.setChecked(mDataSet.get(position).getState());

        holder.nameTextView.setOnClickListener(v -> ((mainActivity) context).setSearchSelected(
            Integer.toString(mDataSet.get(position).getRowId())));

        holder.notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            searchDBHelper dbHelper = new searchDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(searchItemEntry.COLUMN_NAME_NOTIFICATIONSTATE, ((isChecked) ? (1) : (0)));

            String selection = "rowid = ?";
            String[] selectionArgs = {Integer.toString(mDataSet.get(position).getRowId())};

            db.update(searchItemEntry.TABLE_NAME, values, selection, selectionArgs);
            db.close();

            mDataSet.get(position).setState(isChecked);
        });

        holder.deleteButton.setOnClickListener(v -> {
            searchDBHelper dbHelper = new searchDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = "rowid = ?";
            String[] selectionArgs = {Integer.toString(mDataSet.get(position).getRowId())};
            db.delete(searchItemEntry.TABLE_NAME, selection, selectionArgs);
            db.close();

            //This is cheap but it works
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
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
