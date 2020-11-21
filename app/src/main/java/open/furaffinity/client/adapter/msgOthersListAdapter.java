package open.furaffinity.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.fragments.journal;
import open.furaffinity.client.fragments.view;

public class msgOthersListAdapter extends RecyclerView.Adapter<msgOthersListAdapter.ViewHolder> {
    private static final String TAG = msgOthersListAdapter.class.getName();

    private List<HashMap<String, String>> mDataSet;
    private List<String> checkedItems;

    private Context context;

    public msgOthersListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
        checkedItems = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userIcon;
        private final TextView userName;
        private final TextView actionText;
        private final CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            userIcon = itemView.findViewById(R.id.userIcon);
            userName = itemView.findViewById(R.id.userName);
            actionText = itemView.findViewById(R.id.actionText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msgothers_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String actionText = " ";

        if (mDataSet.get(position).containsKey("userIcon")) {
            Glide.with(holder.itemView).load(mDataSet.get(position).get("userIcon")).into(holder.userIcon);

            if (mDataSet.get(position).containsKey("userLink")) {
                holder.userIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((mainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                    }
                });
            }
        } else {
            holder.userIcon.setVisibility(View.GONE);
        }

        if (mDataSet.get(position).containsKey("userName")) {
            holder.userName.setText(mDataSet.get(position).get("userName"));

            if (mDataSet.get(position).containsKey("userLink")) {
                holder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((mainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                    }
                });
            }
        } else {
            holder.userName.setVisibility(View.GONE);
        }

        if (mDataSet.get(position).containsKey("actionText")) {
            actionText += mDataSet.get(position).get("actionText");
        }
        if (mDataSet.get(position).containsKey("postName")) {
            actionText += " " + mDataSet.get(position).get("postName");
        }
        if (mDataSet.get(position).containsKey("time")) {
            actionText += " " + mDataSet.get(position).get("time");
        }

        holder.actionText.setText(actionText);

        if (mDataSet.get(position).containsKey("postLink") && mDataSet.get(position).containsKey("postClass")) {
            holder.actionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataSet.get(position).get("postClass").equals(journal.class.getName())) {
                        ((mainActivity) context).setJournalPath(mDataSet.get(position).get("postLink"));
                    } else if (mDataSet.get(position).get("postClass").equals(view.class.getName())) {
                        ((mainActivity) context).setViewPath(mDataSet.get(position).get("postLink"));
                    }
                }
            });
        }

        if (checkedItems.contains(mDataSet.get(position).get("notificationId"))) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedItems.add(mDataSet.get(position).get("notificationId"));
                } else {
                    checkedItems.remove(mDataSet.get(position).get("notificationId"));
                }
            }
        });
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public void clearChecked() {
        checkedItems = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}