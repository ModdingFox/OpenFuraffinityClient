package open.furaffinity.client.adapter;

import android.content.Intent;
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
import open.furaffinity.client.utilities.messageIds;

public class journalListAdapter extends RecyclerView.Adapter<journalListAdapter.ViewHolder> {
    private List<HashMap<String, String>> mDataSet;

    public journalListAdapter(List<HashMap<String, String>> mDataSetIn) {
        mDataSet = mDataSetIn;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.journalItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), open.furaffinity.client.activity.journalActivity.class);
                intent.putExtra(messageIds.pagePath_MESSAGE, mDataSet.get(position).get("journalPath"));
                v.getContext().startActivity(intent);
            }
        });

        holder.journalTitle.setText(mDataSet.get(position).get("journalTitle"));
        holder.journalDate.setText(mDataSet.get(position).get("journalDate"));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}