package open.furaffinity.client.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import open.furaffinity.client.R;
import open.furaffinity.client.activity.MainActivity;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Context context;
    RefreshListener listener;
    private List<String> checkedItems;
    private boolean isLoggedIn;

    public CommentListAdapter(List<HashMap<String, String>> mDataSetIn, Context context,
                              boolean isLoggedIn) {
        mDataSet = mDataSetIn;
        this.context = context;
        this.isLoggedIn = isLoggedIn;
        checkedItems = new ArrayList<>();
    }

    public void setListener(RefreshListener listener) {
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(
                    R.layout.comment_item,
                    parent,
                    false)
        );
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.commentUserConstraintLayout.setOnClickListener(
            view -> ((MainActivity) context).setUserPath(mDataSet.get(position).get("userLink")));

        Glide.with(holder.itemView).load(mDataSet.get(position).get("userIcon"))
            .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
            .into(holder.commentUserIcon);
        holder.commentUserName.setText(mDataSet.get(position).get("userName"));

        try {
            final Date postDate = new java.util.Date(
                Integer.parseInt(mDataSet.get(position).get("commentDate")) * 1000L);
            final SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd',' yyyy hh:mm a");
            final String formattedDate = sdf.format(postDate);
            holder.commentDate.setText(formattedDate);
        } catch (NumberFormatException e) {
            // eat it and just display the passed data. Yeah its cheap but works
            holder.commentDate.setText(mDataSet.get(position).get("commentDate"));
        }

        holder.comment.loadData(
            "<font color='white'>" + mDataSet.get(position).get("comment") + "</font>",
            "text/html; charset=utf-8", "UTF-8");

        if (isLoggedIn && mDataSet.get(position).containsKey("replyToLink")) {
            holder.replyButton.setVisibility(View.VISIBLE);

            holder.replyButton.setOnClickListener(
                view -> {
                    listener.reply(mDataSet.get(position).get("replyToLink"),
                        mDataSet.get(position).get("userName"));
                });
        }

        if (mDataSet.get(position).containsKey("parentCommentId")) {
            for (int index = 0; index < position; index++) {
                if (mDataSet.get(index).get("commentId")
                    .equals(mDataSet.get(position).get("parentCommentId"))) {
                    int paddingToApply = Integer.parseInt(mDataSet.get(index).get("padding"));
                    paddingToApply += 25;
                    mDataSet.get(position).put("padding", Integer.toString(paddingToApply));

                    holder.commentItemLinearLayout.setPadding(paddingToApply, 0, 0, 0);
                }
            }
        }
        else {
            mDataSet.get(position).put("padding", "0");
        }

        if (mDataSet.get(position).containsKey("checkId")) {
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.checkBox.setChecked(
                checkedItems.contains(mDataSet.get(position).get("checkId")));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedItems.add(mDataSet.get(position).get("checkId"));
                }
                else {
                    checkedItems.remove(mDataSet.get(position).get("checkId"));
                }
            });
        }
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

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public interface RefreshListener {
        void reply(String replyToLink, String userName);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout commentItemLinearLayout;
        private final ConstraintLayout commentUserConstraintLayout;
        private final ImageView commentUserIcon;
        private final TextView commentUserName;
        private final TextView commentDate;
        private final WebView comment;
        private final TextView replyButton;
        private final CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentItemLinearLayout = itemView.findViewById(R.id.commentItemLinearLayout);
            commentUserConstraintLayout = itemView.findViewById(R.id.commentUserConstraintLayout);
            commentUserIcon = itemView.findViewById(R.id.commentUserIcon);
            commentUserName = itemView.findViewById(R.id.commentUserName);
            commentDate = itemView.findViewById(R.id.commentDate);
            comment = itemView.findViewById(R.id.comment);
            comment.setBackgroundColor(Color.TRANSPARENT);
            replyButton = itemView.findViewById(R.id.replyButton);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
