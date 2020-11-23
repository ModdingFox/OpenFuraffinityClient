package open.furaffinity.client.fragmentDrawers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.journalSectionsPagerAdapter;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.historyContract;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class journal extends appFragment {
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

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.journal page;

    private boolean isLoading = false;

    private void saveHistory() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(requireActivity());

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

    @Override
    protected int getLayout() {
        return R.layout.fragment_journal;
    }

    protected void getElements(View rootView) {
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

        watchUser = new FloatingActionButton(requireContext());
        sendNote = new FloatingActionButton(requireContext());

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        //noinspection deprecation
        watchUser.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(watchUser);
        coordinatorLayout.addView(sendNote);

        fab.addButton(watchUser, 1.5f, 270);
        fab.addButton(sendNote, 1.5f, 225);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.journal(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.journal page) {
        journalSectionsPagerAdapter sectionsPagerAdapter = new journalSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
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
        }, ((mainActivity)requireActivity()).getJournalPath());
    }

    protected void updateUIElementListeners(View rootView) {
        journalLinearLayout.setOnClickListener(v -> ((mainActivity)requireActivity()).setUserPath(page.getJournalUserLink()));

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

        sendNote.setOnClickListener(v -> sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser()));
    }
}