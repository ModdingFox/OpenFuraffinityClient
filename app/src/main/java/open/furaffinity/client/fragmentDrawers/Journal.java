package open.furaffinity.client.fragmentDrawers;

import static open.furaffinity.client.utilities.SendPm.sendPM;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.JournalSectionsPagerAdapter;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.sqlite.HistoryContract;
import open.furaffinity.client.sqlite.HistoryDBHelper;
import open.furaffinity.client.submitPages.SubmitGetRequest;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.WebClient;

public class Journal extends AbstractAppFragment {
    private androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;
    private TabLayout tabs;
    private LinearLayout journalLinearLayout;
    private ImageView journalUserIcon;
    private TextView journalUserName;
    private TextView journalTitle;
    private TextView journalDate;
    private ViewPager viewPager;
    private FabCircular fab;
    private FloatingActionButton watchUser;
    private FloatingActionButton sendNote;
    private FloatingActionButton shareLink;

    private LoginCheck loginCheck;
    private open.furaffinity.client.pages.Journal page;
    private boolean isLoading;
    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override public void requestSucceeded(AbstractPage abstractPage) {
            if (((open.furaffinity.client.pages.Journal) abstractPage).getWatchUnWatch() != null
                && ((open.furaffinity.client.pages.Journal) abstractPage).getNoteUser() != null) {
                coordinatorLayout.addView(watchUser);
                coordinatorLayout.addView(sendNote);

                fab.addButton(watchUser, 1.5f, 270);
                fab.addButton(sendNote, 1.5f, 225);

                if (((open.furaffinity.client.pages.Journal) abstractPage).getIsWatching()) {
                    watchUser.setImageResource(R.drawable.ic_menu_user_remove);
                }
                else {
                    watchUser.setImageResource(R.drawable.ic_menu_user_add);
                }
            }

            Glide.with(Journal.this)
                .load(((open.furaffinity.client.pages.Journal) abstractPage).getJournalUserIcon())
                .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                .into(journalUserIcon);
            journalUserName.setText(
                ((open.furaffinity.client.pages.Journal) abstractPage).getJournalUserName());
            journalTitle.setText(
                ((open.furaffinity.client.pages.Journal) abstractPage).getJournalTitle());
            journalDate.setText(
                ((open.furaffinity.client.pages.Journal) abstractPage).getJournalDate());

            saveHistory();
            setupViewPager((open.furaffinity.client.pages.Journal) abstractPage);

            isLoading = false;
        }

        @Override public void requestFailed(AbstractPage abstractPage) {
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for journal", Toast.LENGTH_SHORT)
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

            SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

            // Delete previous versions from history
            final String selection = HistoryContract.historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            final String[] selectionArgs = {page.getPagePath()};
            sqliteDatabase.delete(HistoryContract.historyItemEntry.TABLE_NAME_JOURNAL, selection,
                selectionArgs);

            // Insert into history
            final ContentValues values = new ContentValues();
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_USER,
                page.getJournalUserName());
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_TITLE, page.getJournalTitle());
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(HistoryContract.historyItemEntry.COLUMN_NAME_DATETIME,
                (new Date()).getTime());
            sqliteDatabase.insert(HistoryContract.historyItemEntry.TABLE_NAME_JOURNAL, null, values);

            // Limit history to 512 entries
            sqliteDatabase.execSQL("DELETE FROM "
                + HistoryContract.historyItemEntry.TABLE_NAME_JOURNAL
                + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY "
                + "rowid DESC LIMIT 512))");

            sqliteDatabase.close();
        }

        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(),
            page.getPagePath());
    }

    @Override protected int getLayout() {
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
        shareLink = new FloatingActionButton(requireContext());

        watchUser.setImageResource(R.drawable.ic_menu_user_add);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);
        shareLink.setImageResource(R.drawable.ic_menu_send);

        watchUser.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        sendNote.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        shareLink.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(shareLink);

        fab.addButton(shareLink, 2.6f, 270);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new LoginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.Journal(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.Journal page) {
        final JournalSectionsPagerAdapter sectionsPagerAdapter =
            new JournalSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
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
            page = new open.furaffinity.client.pages.Journal(getActivity(), pageListener,
                ((MainActivity) requireActivity()).getJournalPath());
        }
    }

    protected void updateUiElementListeners(View rootView) {
        journalLinearLayout.setOnClickListener(
            view -> {
                ((MainActivity) requireActivity()).setUserPath(page.getJournalUserLink());
            });

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

        sendNote.setOnClickListener(
            view -> {
                sendPM(getActivity(), getChildFragmentManager(), page.getNoteUser());
            });

        shareLink.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,
                WebClient.getBaseUrl() + page.getPagePath());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("journalPath", page.getPagePath());
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("journalPath")) {
            page = new open.furaffinity.client.pages.Journal(getActivity(), pageListener,
                savedInstanceState.getString("journalPath"));
        }
    }
}