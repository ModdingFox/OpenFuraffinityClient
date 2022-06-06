package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;

public class controlsJournalListAdapter
    extends RecyclerView.Adapter<controlsJournalListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;
    private controlsJournalListAdapterListener listener;

    public controlsJournalListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public void setListener(controlsJournalListAdapterListener controlsJournalListAdapterListener) {
        listener = controlsJournalListAdapterListener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.control_journal_item, parent, false);

        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.linearLayout.setOnClickListener(
            v -> ((mainActivity) context).setJournalPath(mDataSet.get(position).get("postPath")));

        holder.subject.setText(mDataSet.get(position).get("postSubject"));
        holder.date.setText(mDataSet.get(position).get("postDate"));

        holder.updateButton.setOnClickListener(
            v -> listener.updateJournal(mDataSet.get(position).get("editPath")));

        holder.deleteButton.setOnClickListener(
            v -> new open.furaffinity.client.submitPages.submitGetRequest(context,
                new abstractPage.pageListener() {
                    @Override public void requestSucceeded(abstractPage abstractPage) {
                        mDataSet.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Successfully deleted journal", Toast.LENGTH_SHORT)
                            .show();
                    }

                    @Override public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(context, "Failed to delete journal", Toast.LENGTH_SHORT)
                            .show();
                    }
                }, mDataSet.get(position).get("deletePath")).execute());
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public interface controlsJournalListAdapterListener {
        void updateJournal(String editPath);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linearLayout;
        private final TextView subject;
        private final TextView date;
        private final Button updateButton;
        private final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            subject = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
