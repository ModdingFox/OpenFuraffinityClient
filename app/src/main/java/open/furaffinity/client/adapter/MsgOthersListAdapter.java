package open.furaffinity.client.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.fragmentDrawers.Journal;
import open.furaffinity.client.fragmentDrawers.View;

public class MsgOthersListAdapter extends RecyclerView.Adapter<MsgOthersListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;
    private List<String> checkedItems;

    public MsgOthersListAdapter(List<HashMap<String, String>> mDataSetIn, Context context) {
        mDataSet = mDataSetIn;
        this.context = context;
        checkedItems = new ArrayList<>();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.msgothers_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String actionText = " ";

        if (mDataSet.get(position).containsKey("userIcon")) {
            Glide.with(holder.itemView).load(mDataSet.get(position).get("userIcon"))
                .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                .into(holder.userIcon);

            if (mDataSet.get(position).containsKey("userLink")) {
                holder.userIcon.setOnClickListener(view -> {
                    ((MainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                });
            }
        }
        else {
            holder.userIcon.setVisibility(android.view.View.GONE);
        }

        if (mDataSet.get(position).containsKey("userName")) {
            holder.userName.setText(mDataSet.get(position).get("userName"));

            if (mDataSet.get(position).containsKey("userLink")) {
                holder.userName.setOnClickListener(view -> {
                    ((MainActivity) context).setUserPath(mDataSet.get(position).get("userLink"));
                });
            }
        }
        else {
            holder.userName.setVisibility(android.view.View.GONE);
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

        if (mDataSet.get(position).containsKey("postLink")
            && mDataSet.get(position).containsKey("postClass")) {
            holder.actionText.setOnClickListener(view -> {
                if (mDataSet.get(position).get("postClass").equals(Journal.class.getName())) {
                    ((MainActivity) context).setJournalPath(mDataSet.get(position).get("postLink"));
                }
                else if (mDataSet.get(position).get("postClass").equals(View.class.getName())) {
                    ((MainActivity) context).setViewPath(mDataSet.get(position).get("postLink"));
                }
            });
        }

        holder.checkBox.setChecked(
            checkedItems.contains(mDataSet.get(position).get("notificationId")));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItems.add(mDataSet.get(position).get("notificationId"));
            }
            else {
                checkedItems.remove(mDataSet.get(position).get("notificationId"));
            }
        });
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public void clearChecked() {
        checkedItems = new ArrayList<>();
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userIcon;
        private final TextView userName;
        private final TextView actionText;
        private final CheckBox checkBox;

        ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);

            userIcon = itemView.findViewById(R.id.userIcon);
            userName = itemView.findViewById(R.id.userName);
            actionText = itemView.findViewById(R.id.actionText);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
