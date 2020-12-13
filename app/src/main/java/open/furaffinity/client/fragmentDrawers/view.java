package open.furaffinity.client.fragmentDrawers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.viewSectionsPagerAdapter;
import open.furaffinity.client.listener.OnSwipeTouchListener;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.historyContract.historyItemEntry;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.fabCircular;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class view extends appFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private CoordinatorLayout coordinatorLayout;

    private TextView submissionTitle;
    private ImageView submissionImage;
    private LinearLayout submissionUserLinearLayout;
    private ImageView submissionUserIcon;
    private TextView submissionUser;
    private ViewPager viewPager;
    private TabLayout tabs;

    private fabCircular fab;
    private FloatingActionButton submissionFavorite;
    private FloatingActionButton submissionDownload;
    private FloatingActionButton sendNote;

    private loginCheck loginCheck;
    private open.furaffinity.client.pages.view page;
    private boolean isLoading = false;
    private final abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(abstractPage abstractPage) {
            submissionTitle.setText(((open.furaffinity.client.pages.view) abstractPage).getSubmissionTitle());
            Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionImgLink()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionImage);
            Glide.with(view.this).load(((open.furaffinity.client.pages.view) abstractPage).getSubmissionUserIcon()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(submissionUserIcon);
            submissionUser.setText(((open.furaffinity.client.pages.view) abstractPage).getSubmissionUser());

            if (((open.furaffinity.client.pages.view) abstractPage).getIsFav()) {
                submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
            } else {
                submissionFavorite.setImageResource(R.drawable.ic_menu_unfavorite);
            }

            saveHistory();
            setupViewPager(((open.furaffinity.client.pages.view) abstractPage));

            fab.setVisibility(View.VISIBLE);
            isLoading = false;
        }

        @Override
        public void requestFailed(abstractPage abstractPage) {
            fab.setVisibility(View.GONE);
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for view", Toast.LENGTH_SHORT).show();
        }
    };

    private void saveHistory() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(requireActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(requireActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            String[] selectionArgs = {page.getPagePath()};
            db.delete(historyItemEntry.TABLE_NAME_VIEW, selection , selectionArgs);

            //Insert into history
            ContentValues values = new ContentValues();
            values.put(historyItemEntry.COLUMN_NAME_USER, page.getSubmissionUser());
            values.put(historyItemEntry.COLUMN_NAME_TITLE, page.getSubmissionTitle());
            values.put(historyItemEntry.COLUMN_NAME_URL, page.getPagePath());
            values.put(historyItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            db.insert(historyItemEntry.TABLE_NAME_VIEW, null, values);

            //Limit history to 512 entries
            db.execSQL("DELETE FROM " + historyItemEntry.TABLE_NAME_VIEW + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM viewHistory ORDER BY rowid DESC LIMIT 512))");

            db.close();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_view;
    }

    protected void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        submissionTitle = rootView.findViewById(R.id.submissionTitle);
        submissionImage = rootView.findViewById(R.id.submissionImage);
        submissionUserLinearLayout = rootView.findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = rootView.findViewById(R.id.submissionUserIcon);
        submissionUser = rootView.findViewById(R.id.submissionUser);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
        fab = rootView.findViewById(R.id.fab);

        submissionFavorite = new FloatingActionButton(requireContext());
        submissionDownload = new FloatingActionButton(requireContext());
        sendNote = new FloatingActionButton(requireContext());

        submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
        submissionDownload.setImageResource(R.drawable.ic_menu_download);
        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        //noinspection deprecation
        submissionFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        submissionDownload.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        //noinspection deprecation
        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(submissionFavorite);
        coordinatorLayout.addView(submissionDownload);
        coordinatorLayout.addView(sendNote);

        submissionFavorite.setVisibility(View.GONE);
        sendNote.setVisibility(View.GONE);

        fab.addButton(submissionDownload, 1.5f, 270);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            page = new open.furaffinity.client.pages.view(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.view page) {
        viewSectionsPagerAdapter sectionsPagerAdapter = new viewSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    protected void initPages() {
        String pagePath = ((mainActivity) requireActivity()).getViewPath();

        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(View.GONE);
                sendNote.setVisibility(View.GONE);

                if (((loginCheck) abstractPage).getIsLoggedIn()) {
                    fab.addButton(submissionFavorite, 1.5f, 180);
                    fab.addButton(sendNote, 1.5f, 225);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.removeButton(submissionFavorite);
                fab.removeButton(sendNote);

                submissionFavorite.setVisibility(View.GONE);
                sendNote.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
            }
        });

        if (page == null) {
            page = new open.furaffinity.client.pages.view(getActivity(), pageListener, pagePath);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void updateUIElementListeners(View rootView) {
        submissionUserLinearLayout.setOnClickListener(v -> ((mainActivity) requireActivity()).setUserPath(page.getSubmissionUserPage()));

        submissionImage.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                if (page.getNext() != null) {
                    ((mainActivity) requireActivity()).setViewPath(page.getNext());
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    ((mainActivity) requireActivity()).setViewPath(page.getPrev());
                }
            }
        });

        submissionDownload.setOnClickListener(v -> {
            open.furaffinity.client.utilities.downloadContent.downloadSubmission(requireActivity(), requireContext(), page);
        });

        submissionFavorite.setOnClickListener(v -> new open.furaffinity.client.submitPages.submitGetRequest(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fetchPageData();
                Toast.makeText(getActivity(), "Successfully updated favorites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to update favorites", Toast.LENGTH_SHORT).show();
            }
        }, page.getFavUnFav()).execute());

        sendNote.setOnClickListener(v -> sendPM(getActivity(), getChildFragmentManager(), page.getNote()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("viewPath", page.getPagePath());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("viewPath")) {
            page = new open.furaffinity.client.pages.view(getActivity(), pageListener, savedInstanceState.getString("viewPath"));
        }
    }
}