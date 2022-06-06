package open.furaffinity.client.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.fragmentDrawers.Settings;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.pages.User;
import open.furaffinity.client.submitPages.SubmitGetRequest;
import open.furaffinity.client.utilities.DownloadContent;
import open.furaffinity.client.utilities.WebClient;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private final List<HashMap<String, String>> mDataSet;
    private final Activity activity;
    private final Context context;
    private final LoginCheck loginCheck;
    private final boolean showPostInfo;

    public ImageListAdapter(List<HashMap<String, String>> mDataSetIn, Activity activity,
                            Context context) {
        mDataSet = mDataSetIn;
        this.activity = activity;
        this.context = context;

        this.loginCheck =
            new LoginCheck(context, new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {

                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    Toast.makeText(activity, "Failed to detect login", Toast.LENGTH_SHORT).show();
                }
            });
        loginCheck.execute();

        final SharedPreferences sharedPref =
            context.getSharedPreferences(context.getString(R.string.settingsFile),
                Context.MODE_PRIVATE);
        showPostInfo = sharedPref.getBoolean(context.getString(R.string.imageListInfo),
            Settings.imageListInfoDefault);
    }

    @Override public int getItemViewType(int position) {
        if (mDataSet.get(position).containsKey("type")) {
            switch (mDataSet.get(position).get("type")) {
                case "imagelist_item":
                    return 0;
                case "imagelist_advertisement_item":
                    return 1;
                default:
                    return -1;
            }
        }
        else {
            // default to imagelistItem for old stuff. Will go back later and enforce the
            // requirement so this returns -1 later on
            return 0;
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.imagelist_item, parent, false), viewType);
            case 1:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.imagelist_advertisement_item, parent, false), viewType);
            default:
                return null;
        }
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                Glide.with(context).load("https:" + mDataSet.get(position).get("imgUrl"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                    .into(holder.postImage);
                holder.postName.setText(
                    String.format("%s", mDataSet.get(position).get("postTitle")));
                holder.postUser.setText(
                    String.format("By: %s", mDataSet.get(position).get("postUserName")));
                holder.postRating.setText(mDataSet.get(position).get("postRatingCode"));

                holder.menuButton.setEnabled(true);
                holder.menuButton.setOnClickListener(view -> {
                    final PopupMenu popupMenu = new PopupMenu(context, view);
                    final MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.image_result_menu, popupMenu.getMenu());
                    holder.menuButton.setEnabled(false);

                    popupMenu.setOnDismissListener(menu -> holder.menuButton.setEnabled(true));

                    final open.furaffinity.client.pages.View viewPage =
                        new open.furaffinity.client.pages.View(context,
                            new AbstractPage.PageListener() {
                                @Override public void requestSucceeded(AbstractPage abstractPage) {
                                    popupMenu.getMenu().findItem(R.id.menu_download)
                                        .setVisible(true);

                                    if (loginCheck.getIsLoggedIn()) {
                                        final MenuItem favMenuItem =
                                            popupMenu.getMenu().findItem(R.id.menu_favUnfav);
                                        if (((open.furaffinity.client.pages.View) abstractPage)
                                            .getIsFav()) {
                                            favMenuItem.setTitle("UnFav");
                                            favMenuItem.setIcon(R.drawable.ic_menu_unfavorite);
                                        }
                                        else {
                                            favMenuItem.setTitle("Fav");
                                            favMenuItem.setIcon(R.drawable.ic_menu_favorite);
                                        }
                                        popupMenu.getMenu().findItem(R.id.menu_favUnfav)
                                            .setVisible(true);
                                    }
                                }

                                @Override public void requestFailed(AbstractPage abstractPage) {
                                    Toast.makeText(context,
                                        "Failed to get submission page for menu",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }, mDataSet.get(position).get("postPath"));

                    final User userPage =
                        new User(context, new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                final MenuItem watchMenuItem =
                                    popupMenu.getMenu().findItem(R.id.menu_watchUnWatch);
                                if (((User) abstractPage).getIsWatching()) {
                                    watchMenuItem.setTitle("UnWatch");
                                    watchMenuItem.setIcon(R.drawable.ic_menu_user_remove);
                                }
                                else {
                                    watchMenuItem.setTitle("Watch");
                                    watchMenuItem.setIcon(R.drawable.ic_menu_user_add);
                                }
                                popupMenu.getMenu().findItem(R.id.menu_watchUnWatch)
                                    .setVisible(true);
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(context, "Failed to get user page for menu",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, mDataSet.get(position).get("postUserPath"));

                    viewPage.execute();

                    if (loginCheck.getIsLoggedIn()
                        && mDataSet.get(position).containsKey("postUserPath")
                        && !loginCheck.getUserName()
                            .equals(mDataSet.get(position).get("postUserName"))) {
                        userPage.execute();
                    }

                    if (mDataSet.get(position).containsKey("postPath")) {
                        popupMenu.getMenu().findItem(R.id.menu_shareSubmission).setVisible(true);
                    }

                    if (mDataSet.get(position).containsKey("postUserPath")) {
                        popupMenu.getMenu().findItem(R.id.menu_shareUser).setVisible(true);
                    }

                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(item -> {
                        final Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");

                        switch (item.getItemId()) {
                            case R.id.menu_download:
                                DownloadContent.downloadSubmission(
                                    activity, context, viewPage);
                                return true;
                            case R.id.menu_shareSubmission:
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                    WebClient.getBaseUrl()
                                        + mDataSet.get(position).get("postPath"));
                                context.startActivity(
                                    Intent.createChooser(sharingIntent, "Share via"));
                                return true;
                            case R.id.menu_shareUser:
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                    WebClient.getBaseUrl()
                                        + mDataSet.get(position).get("postUserPath"));
                                context.startActivity(
                                    Intent.createChooser(sharingIntent, "Share via"));
                                return true;
                            case R.id.menu_favUnfav:
                                new SubmitGetRequest(context,
                                    new AbstractPage.PageListener() {
                                        @Override
                                        public void requestSucceeded(AbstractPage abstractPage) {
                                            Toast.makeText(activity,
                                                "Successfully updated favorites",
                                                Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void requestFailed(AbstractPage abstractPage) {
                                            Toast.makeText(activity, "Failed to update favorites",
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }, viewPage.getFavUnFav()).execute();
                                return true;
                            case R.id.menu_watchUnWatch:
                                new SubmitGetRequest(context,
                                    new AbstractPage.PageListener() {
                                        @Override
                                        public void requestSucceeded(AbstractPage abstractPage) {
                                            Toast.makeText(activity, "Successfully updated watches",
                                                Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void requestFailed(AbstractPage abstractPage) {
                                            Toast.makeText(activity, "Failed to update watches",
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }, userPage.getWatchUnWatch()).execute();
                                return true;
                            default:
                                return false;
                        }
                    });
                });

                if (!showPostInfo) {
                    holder.imageListPostInfo.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(view -> {
                    ((MainActivity) context).setViewPath(mDataSet.get(position).get("postPath"));
                });
                break;
            case 1:
                new AsyncTask<Void, Void, Void>() {
                    @Override protected Void doInBackground(Void... voids) {
                        final WebClient webClient =
                            new WebClient(context);
                        final Uri uri = Uri.parse(mDataSet.get(position).get("beacon"));
                        HashMap<String, String> params = new HashMap<>();
                        for (String paramater : uri.getQueryParameterNames()) {
                            params.put(paramater, uri.getQueryParameter(paramater));
                        }
                        String baseUrl =
                            uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
                        webClient.sendPostRequest(baseUrl, params);
                        return null;
                    }
                }.execute();

                Glide.with(context).load(mDataSet.get(position).get("image"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                    .into(holder.postImage);
                holder.postRating.setText("AD");
                holder.itemView.setOnClickListener(view -> {
                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mDataSet.get(position).get("link")));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView postImage;
        private final ConstraintLayout imageListPostInfo;
        private final TextView postName;
        private final TextView postUser;
        private final TextView postRating;
        private final ImageButton menuButton;

        ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case 0:
                    postImage = itemView.findViewById(R.id.imageListCardViewPostImage);
                    imageListPostInfo = itemView.findViewById(R.id.imageListPostInfo);
                    postName = itemView.findViewById(R.id.imageListCardPostName);
                    postUser = itemView.findViewById(R.id.imageListCardPostUser);
                    postRating = itemView.findViewById(R.id.imageListCardPostRating);
                    menuButton = itemView.findViewById(R.id.menuButton);
                    break;
                case 1:
                    postImage = itemView.findViewById(R.id.imageListCardViewPostImage);
                    imageListPostInfo = null;
                    postName = null;
                    postUser = null;
                    postRating = itemView.findViewById(R.id.imageListCardPostRating);
                    menuButton = null;
                    break;
                default:
                    postImage = null;
                    imageListPostInfo = null;
                    postName = null;
                    postUser = null;
                    postRating = null;
                    menuButton = null;
                    break;
            }
        }
    }
}
