package open.furaffinity.client.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;

public class JournalListAdapter extends RecyclerView.Adapter<JournalListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;

    public JournalListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.journal_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.journalItemLinearLayout.setOnClickListener(
            view -> {
                ((MainActivity) context).setJournalPath(mDataSet.get(position).get("journalPath"));
            });

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
