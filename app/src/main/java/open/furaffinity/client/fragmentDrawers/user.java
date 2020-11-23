package open.furaffinity.client.fragmentDrawers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.userSectionsPagerAdapter;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class user extends appFragment {
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

    private fabCircular fab;
    private FloatingActionButton watchUser;
    private FloatingActionButton blockUser;
    private FloatingActionButton sendNote;

    private String currentPage;
    private String currentPagePath = null;

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.user page;

    private boolean isLoading = false;

    private void saveHistory() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(requireActivity());

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = historyContract.historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            String[] selectionArgs = {page.getPagePath()};
            db.delete(historyContract.historyItemEntry.TABLE_NAME_USER, selection, selectionArgs);

            //Insert into history
            ContentValues values = new ContentValues();
            values.put(historyContract.historyItemEntry.COLUMN_NAME_USER, page.getUserName());
            values.put(historyContract.historyItemEntry.COLUMN_NAME_TITLE, "");
            values.put(historyContract.historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(historyContract.historyItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            db.insert(historyContract.historyItemEntry.TABLE_NAME_USER, null, values);

            //Limit history to 512 entries
            db.execSQL("DELETE FROM " + historyContract.historyItemEntry.TABLE_NAME_USER + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY rowid DESC LIMIT 512))");

            db.close();
        }
    }

    @Override
    protected int getLayout() {
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

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        blockUser.setImageResource(R.drawable.ic_menu_user_block);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        //noinspection deprecation
        watchUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        blockUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(watchUser);
        coordinatorLayout.addView(blockUser);
        coordinatorLayout.addView(sendNote);

        fab.addButton(watchUser, 1.5f, 270);
        fab.addButton(sendNote, 1.5f, 225);
        fab.addButton(blockUser, 1.5f, 180);
    }

    private String getPagePath() {
        currentPagePath = ((mainActivity)requireActivity()).getUserPath();
        String result = currentPagePath;

        Matcher userMatcher = Pattern.compile("/(user|gallery|scraps|favorites|journals|commissions|watchlist/to|watchlist/by)/([^/]+)").matcher(result);
        if (userMatcher.find()) {
            result = "/user/" + userMatcher.group(2);
        }
        currentPage = userMatcher.group(1);

        return result;
    }

    protected void fetchPageData() {
        if(!isLoading) {
            isLoading = true;

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.user(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.user page) {
        userSectionsPagerAdapter userSectionsPagerAdapter = new userSectionsPagerAdapter(this.requireActivity(), getChildFragmentManager(), page, currentPage, currentPagePath);
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
        }
    }

    protected void initPages() {
        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((loginCheck)abstractPage).getIsLoggedIn()) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
            }
        });

        page = new open.furaffinity.client.pages.user(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if(((open.furaffinity.client.pages.user)abstractPage).getBlockUnBlock() != null && ((open.furaffinity.client.pages.user)abstractPage).getWatchUnWatch() != null && ((open.furaffinity.client.pages.user)abstractPage).getNoteUser() != null) {
                    if (page.getIsWatching()) {
                        watchUser.setImageResource(R.drawable.ic_menu_user_remove);
                    } else {
                        watchUser.setImageResource(R.drawable.ic_menu_user_add);
                    }

                    if (page.getIsBlocked()) {
                        blockUser.setImageResource(R.drawable.ic_menu_user_unblock);
                    } else {
                        blockUser.setImageResource(R.drawable.ic_menu_user_block);
                    }
                }

                userName.setText(((open.furaffinity.client.pages.user)abstractPage).getUserName());
                userAccountStatus.setText(((open.furaffinity.client.pages.user)abstractPage).getUserAccountStatus());
                userAccountStatusLine.setText(((open.furaffinity.client.pages.user)abstractPage).getUserAccountStatusLine());
                Glide.with(user.this).load(((open.furaffinity.client.pages.user)abstractPage).getUserIcon()).into(userIcon);
                userViews.setText(((open.furaffinity.client.pages.user)abstractPage).getUserViews());
                userSubmissions.setText(((open.furaffinity.client.pages.user)abstractPage).getUserSubmissions());
                userFavs.setText(((open.furaffinity.client.pages.user)abstractPage).getUserFavs());
                userCommentsEarned.setText(((open.furaffinity.client.pages.user)abstractPage).getUserCommentsEarned());
                userCommentsMade.setText(((open.furaffinity.client.pages.user)abstractPage).getUserCommentsMade());
                userJournals.setText(((open.furaffinity.client.pages.user)abstractPage).getUserJournals());

                saveHistory();
                setupViewPager(((open.furaffinity.client.pages.user)abstractPage));

                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for user", Toast.LENGTH_SHORT).show();
            }
        }, getPagePath());
    }

    protected void updateUIElementListeners(View rootView) {
        watchUser.setOnClickListener(v -> new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fetchPageData();
                Toast.makeText(getActivity(), "Successfully updated watches", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to update watches", Toast.LENGTH_SHORT).show();
            }
        }, page.getWatchUnWatch()).execute());

        blockUser.setOnClickListener(v -> new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fetchPageData();
                Toast.makeText(getActivity(), "Successfully updated block status", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to update block status", Toast.LENGTH_SHORT).show();
            }
        }, page.getBlockUnBlock()).execute());

        sendNote.setOnClickListener(v -> sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser()));
    }
}