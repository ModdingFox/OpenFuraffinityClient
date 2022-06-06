package open.furaffinity.client.fragmentDrawers;

import static open.furaffinity.client.utilities.SendPm.sendPM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.Date;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.UserSectionsPagerAdapter;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.sqlite.HistoryContract;
import open.furaffinity.client.sqlite.HistoryDBHelper;
import open.furaffinity.client.submitPages.SubmitGetRequest;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.WebClient;

public class User extends AbstractAppFragment {
    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;

    private TextView userName;
    private TextView userAccountStatus;
    private TextView userAccountStatusLine;
    private ImageView userIcon;
    private TextView userViews;
    private TextView userSubmissions;
    private TextView userFavs;
    private TextView userCommentsEarned;
    private TextView userCommentsMade;
    private TextView userJournals;
    private ViewPager viewPager;
    private TabLayout tabs;

    private FabCircular fab;
    private FloatingActionButton watchUser;
    private FloatingActionButton blockUser;
    private FloatingActionButton sendNote;
    private FloatingActionButton shareLink;

    private String currentPage;
    private String currentPagePath;

    private LoginCheck loginCheck;
    private open.furaffinity.client.pages.User page;
    private boolean isLoading;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            if (((open.furaffinity.client.pages.User) abstractPage).getBlockUnBlock() != null
                && ((open.furaffinity.client.pages.User) abstractPage).getWatchUnWatch() != null
                && ((open.furaffinity.client.pages.User) abstractPage).getNoteUser() != null) {
                coordinatorLayout.addView(watchUser);
                coordinatorLayout.addView(blockUser);
                coordinatorLayout.addView(sendNote);

                fab.addButton(watchUser, 1.5f, 270);
                fab.addButton(sendNote, 1.5f, 225);
                fab.addButton(blockUser, 1.5f, 180);

                if (page.getIsWatching()) {
                    watchUser.setImageResource(R.drawable.ic_menu_user_remove);
                }
                else {
                    watchUser.setImageResource(R.drawable.ic_menu_user_add);
                }

                if (page.getIsBlocked()) {
                    blockUser.setImageResource(R.drawable.ic_menu_user_unblock);
                }
                else {
                    blockUser.setImageResource(R.drawable.ic_menu_user_block);
                }
            }

            userName.setText(((open.furaffinity.client.pages.User) abstractPage).getUserName());
            userAccountStatus.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserAccountStatus());
            userAccountStatusLine.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserAccountStatusLine());
            Glide.with(User.this)
                .load(((open.furaffinity.client.pages.User) abstractPage).getUserIcon())
                .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                .into(userIcon);
            userViews.setText(((open.furaffinity.client.pages.User) abstractPage).getUserViews());
            userSubmissions.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserSubmissions());
            userFavs.setText(((open.furaffinity.client.pages.User) abstractPage).getUserFavs());
            userCommentsEarned.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserCommentsEarned());
            userCommentsMade.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserCommentsMade());
            userJournals.setText(
                ((open.furaffinity.client.pages.User) abstractPage).getUserJournals());

            saveHistory();
            setupViewPager((open.furaffinity.client.pages.User) abstractPage);

            isLoading = false;
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for user", Toast.LENGTH_SHORT)
                .show();
        }
    };

    private void saveHistory() {
        final SharedPreferences sharedPref =
            requireActivity().getSharedPreferences(getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting),
            Settings.trackHistoryDefault)) {
            final HistoryDBHelper dbHelper = new HistoryDBHelper(requireActivity());

            final SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            final String selection = HistoryContract.historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            final String[] selectionArgs = {page.getPagePath()};
            sqliteDatabase.delete(HistoryContract.historyItemEntry.TABLE_NAME_USER, selection, selectionArgs);

            // Insert into history
            final ContentValues values = new ContentValues();
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_USER, page.getUserName());
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_TITLE, "");
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_DATETIME,
                (new Date()).getTime());
            sqliteDatabase.insert(HistoryContract.historyItemEntry.TABLE_NAME_USER, null, values);

            // Limit history to 512 entries
            sqliteDatabase.execSQL("DELETE FROM " + HistoryContract.historyItemEntry.TABLE_NAME_USER
                + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY "
                + "rowid DESC LIMIT 512))");

            sqliteDatabase.close();
        }

        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(),
            page.getPagePath());
    }

    @Override protected int getLayout() {
        return R.layout.fragment_user;
    }

    protected void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        userName = rootView.findViewById(R.id.userName);
        userAccountStatus = rootView.findViewById(R.id.userAccountStatus);
        userAccountStatusLine = rootView.findViewById(R.id.userAccountStatusLine);
        userIcon = rootView.findViewById(R.id.userIcon);
        userViews = rootView.findViewById(R.id.userViews);
        userSubmissions = rootView.findViewById(R.id.userSubmissions);
        userFavs = rootView.findViewById(R.id.userFavs);
        userCommentsEarned = rootView.findViewById(R.id.userCommentsEarned);
        userCommentsMade = rootView.findViewById(R.id.userCommentsMade);
        userJournals = rootView.findViewById(R.id.userJournals);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);

        fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        watchUser = new FloatingActionButton(requireContext());
        blockUser = new FloatingActionButton(requireContext());
        sendNote = new FloatingActionButton(requireContext());
        shareLink = new FloatingActionButton(requireContext());

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        blockUser.setImageResource(R.drawable.ic_menu_user_block);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);
        shareLink.setImageResource(R.drawable.ic_menu_send);

        watchUser.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        blockUser.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        sendNote.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        shareLink.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(shareLink);
        fab.addButton(shareLink, 2.6f, 270);
    }

    private String getPagePath() {
        currentPagePath = ((MainActivity) requireActivity()).getUserPath();
        String result = currentPagePath;

        final Matcher userMatcher = Pattern.compile(
                "/(user|gallery|scraps|favorites|journals|commissions|watchlist/to|watchlist/by)/"
                    + "([^/]+)")
            .matcher(result);
        if (userMatcher.find()) {
            result = "/user/" + userMatcher.group(2);
        }
        currentPage = userMatcher.group(1);

        return result;
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new LoginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.User(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.User page) {
        final UserSectionsPagerAdapter userSectionsPagerAdapter =
            new UserSectionsPagerAdapter(this.requireActivity(), getChildFragmentManager(), page,
                currentPage, currentPagePath);
        viewPager.setAdapter(userSectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);

        switch (currentPage) {
            case "user":
                viewPager.setCurrentItem(0);
                break;
            case "gallery":
                viewPager.setCurrentItem(1);
                break;
            case "scraps":
                viewPager.setCurrentItem(2);
                break;
            case "favorites":
                viewPager.setCurrentItem(3);
                break;
            case "journals":
                viewPager.setCurrentItem(4);
                break;
            case "commissions":
                viewPager.setCurrentItem(5);
                break;
            case "watchlist/to":
                viewPager.setCurrentItem(6);
                break;
            case "watchlist/by":
                viewPager.setCurrentItem(7);
                break;
            default:
                break;
        }
    }

    protected void initPages() {
        loginCheck = new LoginCheck(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                if (((LoginCheck) abstractPage).getIsLoggedIn()) {
                    fab.setVisibility(View.VISIBLE);
                }
                else {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load data for loginCheck",
                    Toast.LENGTH_SHORT).show();
            }
        });

        if (page == null) {
            page =
                new open.furaffinity.client.pages.User(getActivity(), pageListener, getPagePath());
        }
    }

    protected void updateUiElementListeners(View rootView) {
        watchUser.setOnClickListener(
            view -> {
                new SubmitGetRequest(getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            fetchPageData();
                            Toast.makeText(getActivity(), "Successfully updated watches",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to update watches",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, page.getWatchUnWatch()).execute();
            });

        blockUser.setOnClickListener(
            view -> {
                new SubmitGetRequest(getActivity(), new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            fetchPageData();
                            Toast.makeText(getActivity(), "Successfully updated block status",
                                Toast.LENGTH_SHORT).show();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to update block status",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, page.getBlockUnBlock()).execute();
            });

        sendNote.setOnClickListener(
            view -> {
                sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser());
            });

        shareLink.setOnClickListener(view -> {
            final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,
                WebClient.getBaseUrl() + page.getPagePath());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userPath", page.getPagePath());
        outState.putString("currentPage", currentPage);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("userPath")
            && savedInstanceState.containsKey("currentPage")) {
            page = new open.furaffinity.client.pages.User(getActivity(), pageListener,
                savedInstanceState.getString("userPath"));
            currentPage = savedInstanceState.getString("currentPage");
        }
    }
}