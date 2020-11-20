package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class stringListAdapter extends RecyclerView.Adapter<stringListAdapter.ViewHolder> {
    private static final String TAG = stringListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private Context context;

    public stringListAdapter(ArrayList<String> mDataSetIn, Context context) {
        mDataSet = new ArrayList<>();
        this.context = context;

        for (String currentItem : mDataSetIn) {
            HashMap<String, String> newDataItem = new HashMap<>();
            newDataItem.put("item", currentItem);
            mDataSet.add(newDataItem);
        }
    }

    public stringListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.text_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);

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
                    } else if (mDataSet.get(position).get("class").equals(open.furaffinity.client.fragmentsOld.view.class.getName())) {
                        ((mainActivity) context).setViewPath(mDataSet.get(position).get("path"));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
