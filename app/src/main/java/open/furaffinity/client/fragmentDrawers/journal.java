package open.furaffinity.client.fragmentDrawers;

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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.journalSectionsPagerAdapter;
import open.furaffinity.client.pages.loginCheck;
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
    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.journal page;

    private boolean isLoading = false;

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
        fab.setVisibility(View.GONE);

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

    private void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.journal(page);
            page.execute();
        }
    }

    private void setupViewPager(open.furaffinity.client.pages.journal page) {
        journalSectionsPagerAdapter sectionsPagerAdapter = new journalSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    private void initPages(String pagePath) {
        webClient = new webClient(this.getActivity());

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

        page = new open.furaffinity.client.pages.journal(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((open.furaffinity.client.pages.journal)abstractPage).getWatchUnWatch() != null && ((open.furaffinity.client.pages.journal)abstractPage).getNoteUser() != null) {
                    if (((open.furaffinity.client.pages.journal)abstractPage).getIsWatching()) {
                        watchUser.setImageResource(R.drawable.ic_menu_user_remove);
                    } else {
                        watchUser.setImageResource(R.drawable.ic_menu_user_add);
                    }
                }

                Glide.with(journal.this).load(((open.furaffinity.client.pages.journal)abstractPage).getJournalUserIcon()).into(journalUserIcon);
                journalUserName.setText(((open.furaffinity.client.pages.journal)abstractPage).getJournalUserName());
                journalTitle.setText(((open.furaffinity.client.pages.journal)abstractPage).getJournalTitle());
                journalDate.setText(((open.furaffinity.client.pages.journal)abstractPage).getJournalDate());

                saveHistory();
                setupViewPager(((open.furaffinity.client.pages.journal)abstractPage));

                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for journal", Toast.LENGTH_SHORT).show();
            }
        }, pagePath);
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

                    fetchPageData();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_journal, container, false);
        getElements(rootView);
        initPages(((mainActivity) getActivity()).getJournalPath());
        fetchPageData();
        updateUIElementListeners();
        return rootView;
    }
}