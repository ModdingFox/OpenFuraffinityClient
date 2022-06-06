package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;

public class journalListAdapter extends RecyclerView.Adapter<journalListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public journalListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.journalItemLinearLayout.setOnClickListener(
            v -> ((mainActivity) context).setJournalPath(
                mDataSet.get(position).get("journalPath")));

        holder.journalTitle.setText(mDataSet.get(position).get("journalTitle"));
        holder.journalDate.setText(mDataSet.get(position).get("journalDate"));
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout journalItemLinearLayout;
        private final TextView journalTitle;
        private final TextView journalDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            journalItemLinearLayout = itemView.findViewById(R.id.journalItemLinearLayout);
            journalTitle = itemView.findViewById(R.id.journalTitle);
            journalDate = itemView.findViewById(R.id.journalDate);
        }
    }
}