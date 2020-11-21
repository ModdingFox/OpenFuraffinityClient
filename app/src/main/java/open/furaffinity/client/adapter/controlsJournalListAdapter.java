package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.utilities.webClient;

public class controlsJournalListAdapter extends RecyclerView.Adapter<controlsJournalListAdapter.ViewHolder> {
    private static final String TAG = controlsJournalListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public controlsJournalListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public interface controlsJournalListAdapterListener {
        public void updateJournal(String editPath);
    }

    private controlsJournalListAdapterListener listener;

    public void setListener(controlsJournalListAdapterListener controlsJournalListAdapterListener) {
        listener = controlsJournalListAdapterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.control_journal_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) context).setJournalPath(mDataSet.get(position).get("postPath"));
            }
        });

        holder.subject.setText(mDataSet.get(position).get("postSubject"));
        holder.date.setText(mDataSet.get(position).get("postDate"));

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.updateJournal(mDataSet.get(position).get("editPath"));
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + mDataSet.get(position).get("deletePath"));
                            return null;
                        }
                    }.execute(new webClient(context)).get();

                    mDataSet.remove(position);
                    notifyDataSetChanged();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not delete journal: ", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
