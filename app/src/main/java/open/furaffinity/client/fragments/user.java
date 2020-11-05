package open.furaffinity.client.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.adapter.userSectionsPagerAdapter;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class user extends Fragment {
    private static final String TAG = user.class.getName();

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

    private webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;
    private open.furaffinity.client.pages.user page;

    private void saveHistory() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(getActivity());

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

    private void getElements(View rootView) {
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
        watchUser = new FloatingActionButton(getContext());
        blockUser = new FloatingActionButton(getContext());
        sendNote = new FloatingActionButton(getContext());

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        blockUser.setImageResource(R.drawable.ic_menu_user_block);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        watchUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        blockUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(watchUser);
        coordinatorLayout.addView(blockUser);
        coordinatorLayout.addView(sendNote);

        fab.addButton(watchUser, 1.5f, 270);
        fab.addButton(sendNote, 1.5f, 225);
        fab.addButton(blockUser, 1.5f, 180);
    }

    private String getPagePath() {
        currentPagePath = ((mainActivity) getActivity()).getUserPath();
        String result = currentPagePath;

        Matcher userMatcher = Pattern.compile("\\/(user|gallery|scraps|favorites|journals|commissions|watchlist\\/to|watchlist\\/by)\\/([^\\/]+)").matcher(result);
        if (userMatcher.find()) {
            result = "/user/" + userMatcher.group(2);
        }
        currentPage = userMatcher.group(1);

        return result;
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        page = new open.furaffinity.client.pages.user(pagePath);
    }

    private void fetchPageData() {
        loginTest = new loginTest();
        try {
            loginTest.execute(webClient).get();
            page.execute(webClient).get();
            saveHistory();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    private void checkPageLoaded() {
        if (!page.getIsLoaded()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("There was an issue loading the data from FurAffinity. Returning you to where you came from.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void updateUIElements() {
        if(loginTest.getIsLoggedIn() && page.getBlockUnBlock() != null && page.getWatchUnWatch() != null && page.getNoteUser() != null) {
            fab.setVisibility(View.VISIBLE);

            if(page.getIsWatching()) {
                watchUser.setImageResource(R.drawable.ic_menu_user_remove);
            } else {
                watchUser.setImageResource(R.drawable.ic_menu_user_add);
            }

            if(page.getIsBlocked()) {
                blockUser.setImageResource(R.drawable.ic_menu_user_unblock);
            } else {
                blockUser.setImageResource(R.drawable.ic_menu_user_block);
            }
        } else {
            fab.setVisibility(View.GONE);
        }

        userName.setText(page.getUserName());
        userAccountStatus.setText(page.getUserAccountStatus());
        userAccountStatusLine.setText(page.getUserAccountStatusLine());
        Glide.with(this).load(page.getUserIcon()).into(userIcon);
        userViews.setText(page.getUserViews());
        userSubmissions.setText(page.getUserSubmissions());
        userFavs.setText(page.getUserFavs());
        userCommentsEarned.setText(page.getUserCommentsEarned());
        userCommentsMade.setText(page.getUserCommentsMade());
        userJournals.setText(page.getUserJournals());
    }

    private void updateUIElementListeners(View rootView) {
        watchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getWatchUnWatch());
                            return null;
                        }
                    }.execute(webClient).get();

                    initClientAndPage(page.getPagePath());
                    fetchPageData();
                    updateUIElements();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not watch/unwatch user: ", e);
                }
            }
        });

        blockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getBlockUnBlock());
                            return null;
                        }
                    }.execute(webClient).get();

                    initClientAndPage(page.getPagePath());
                    fetchPageData();
                    updateUIElements();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not block/unblock user: ", e);
                }
            }
        });

        sendNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser());
            }
        });
    }

    private void setupViewPager() {
        userSectionsPagerAdapter userSectionsPagerAdapter = new userSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page, currentPage, currentPagePath);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        getElements(rootView);
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners(rootView);
        setupViewPager();
        return rootView;
    }
}