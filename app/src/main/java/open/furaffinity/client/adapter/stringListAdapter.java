package open.furaffinity.client.adapter;

import android.content.Intent;
import android.util.Log;
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

public class stringListAdapter extends RecyclerView.Adapter<stringListAdapter.ViewHolder> {
    private static final String TAG = stringListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;

    public stringListAdapter(ArrayList<String> mDataSetIn) {
        mDataSet = new ArrayList<>();

        for (String currentItem : mDataSetIn) {
            HashMap<String, String> newDataItem = new HashMap<>();
            newDataItem.put("item", currentItem);
            mDataSet.add(newDataItem);
        }
    }

    public stringListAdapter(List<HashMap<String, String>> mDataSetIn) {
        mDataSet = mDataSetIn;
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

        if (mDataSet.get(position).get("path") != null && mDataSet.get(position).get("class") != null && mDataSet.get(position).get("messageId") != null) {
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), Class.forName(mDataSet.get(position).get("class")));
                        intent.putExtra(mDataSet.get(position).get("messageId"), mDataSet.get(position).get("path"));
                        v.getContext().startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "onClick: ", e);
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
