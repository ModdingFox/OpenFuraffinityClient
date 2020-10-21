package open.furaffinity.client.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;

public class checkboxListAdapter extends RecyclerView.Adapter<checkboxListAdapter.ViewHolder> {
    private static final String TAG = checkboxListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private List<String> checkedItems = new ArrayList<>();

    public checkboxListAdapter(ArrayList<String> mDataSetIn) {
        mDataSet = new ArrayList<>();

        for (String currentItem : mDataSetIn) {
            HashMap<String, String> newDataItem = new HashMap<>();
            newDataItem.put("item", currentItem);
            mDataSet.add(newDataItem);
        }
    }

    public checkboxListAdapter(List<HashMap<String, String>> mDataSetIn) {
        mDataSet = mDataSetIn;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setText(mDataSet.get(position).get("item"));

        holder.item.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedItems.add(holder.item.getText().toString());
                } else {
                    int removeIndex = -1;
                    for(int i = 0; i < checkedItems.size(); i++) {
                        if(checkedItems.get(i).equals(holder.item.getText().toString())) {
                            removeIndex = i;
                            break;
                        }
                    }
                    if(removeIndex > -1) { checkedItems.remove(removeIndex); }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public List<String> getCheckedItems() { return checkedItems; }
}
