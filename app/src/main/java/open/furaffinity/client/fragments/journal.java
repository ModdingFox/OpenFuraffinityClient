package open.furaffinity.client.fragments;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.journalSectionsPagerAdapter;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class journal extends Fragment {
    private static final String TAG = journal.class.getName();

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;

    private LinearLayout journalLinearLayout;
    private ImageView journalUserIcon;
    private TextView journalUserName;
    private TextView journalTitle;
    private TextView journalDate;
    private ViewPager viewPager;
    TabLayout tabs;

    private fabCircular fab;
    private FloatingActionButton watchUser;
    private FloatingActionButton sendNote;

    private webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;
    private open.furaffinity.client.pages.journal page;

    private void saveHistory() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(getActivity());

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = historyContract.historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            String[] selectionArgs = {page.getPagePath()};
            db.delete(historyContract.historyItemEntry.TABLE_NAME_JOURNAL, selection, selectionArgs);

            //Insert into history
            ContentValues values = new ContentValues();
            values.put(historyContract.historyItemEntry.COLUMN_NAME_USER, page.getJournalUserName());
            values.put(historyContract.historyItemEntry.COLUMN_NAME_TITLE, page.getJournalTitle());
            values.put(historyContract.historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(historyContract.historyItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            db.insert(historyContract.historyItemEntry.TABLE_NAME_JOURNAL, null, values);

            //Limit history to 512 entries
            db.execSQL("DELETE FROM " + historyContract.historyItemEntry.TABLE_NAME_JOURNAL + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY rowid DESC LIMIT 512))");

            db.close();
        }
    }

    private void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        journalLinearLayout = rootView.findViewById(R.id.journalLinearLayout);
        journalUserIcon = rootView.findViewById(R.id.journalUserIcon);
        journalUserName = rootView.findViewById(R.id.journalUserName);
        journalTitle = rootView.findViewById(R.id.journalTitle);
        journalDate = rootView.findViewById(R.id.journalDate);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);

        fab = rootView.findViewById(R.id.fab);
        watchUser = new FloatingActionButton(getContext());
        sendNote = new FloatingActionButton(getContext());

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        watchUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(watchUser);
        coordinatorLayout.addView(sendNote);

        fab.addButton(watchUser, 1.5f, 270);
        fab.addButton(sendNote, 1.5f, 225);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        page = new open.furaffinity.client.pages.journal(pagePath);
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

    }

    private void updateUIElements() {
        if(loginTest.getIsLoggedIn() && page.getWatchUnWatch() != null && page.getNoteUser() != null) {
            fab.setVisibility(View.VISIBLE);

            if(page.getIsWatching()) {
                watchUser.setImageResource(R.drawable.ic_menu_user_remove);
            } else {
                watchUser.setImageResource(R.drawable.ic_menu_user_add);
            }
        } else {
            fab.setVisibility(View.GONE);
        }

        Glide.with(this).load(page.getJournalUserIcon()).into(journalUserIcon);
        journalUserName.setText(page.getJournalUserName());
        journalTitle.setText(page.getJournalTitle());
        journalDate.setText(page.getJournalDate());
    }

    private void updateUIElementListeners() {
        journalLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setUserPath(page.getJournalUserLink());
            }
        });

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

        sendNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser());
            }
        });
    }

    private void setupViewPager() {
        journalSectionsPagerAdapter sectionsPagerAdapter = new journalSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_journal, container, false);
        getElements(rootView);
        initClientAndPage(((mainActivity) getActivity()).getJournalPath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
        return rootView;
    }
}