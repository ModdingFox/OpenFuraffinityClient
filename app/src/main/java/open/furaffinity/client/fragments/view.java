package open.furaffinity.client.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.viewSectionsPagerAdapter;
import open.furaffinity.client.listener.OnSwipeTouchListener;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.sqlite.historyContract.historyItemEntry;
import open.furaffinity.client.sqlite.historyDBHelper;
import open.furaffinity.client.utilities.WrapContentViewPager;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

public class view extends Fragment {
    private static final String TAG = view.class.getName();

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

    private webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;
    private open.furaffinity.client.pages.view page;

    private void saveHistory() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(getActivity().getString(R.string.trackHistorySetting), settings.trackHistoryDefault)) {
            historyDBHelper dbHelper = new historyDBHelper(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = historyItemEntry.COLUMN_NAME_URL + " LIKE ?";
            String[] selectionArgs = { page.getPagePath()};
            db.delete(historyItemEntry.TABLE_NAME_VIEW, selection, selectionArgs);

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

    private void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        submissionTitle = rootView.findViewById(R.id.submissionTitle);
        submissionImage = rootView.findViewById(R.id.submissionImage);
        submissionUserLinearLayout = rootView.findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = rootView.findViewById(R.id.submissionUserIcon);
        submissionUser = rootView.findViewById(R.id.submissionUser);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
        fab = rootView.findViewById(R.id.fab);

        submissionFavorite = new FloatingActionButton(getContext());
        submissionDownload = new FloatingActionButton(getContext());

        submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
        submissionDownload.setImageResource(R.drawable.ic_menu_download);

        submissionFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        submissionDownload.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(submissionFavorite);
        coordinatorLayout.addView(submissionDownload);

        fab.addButton(submissionDownload, 1.5f, 270);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        page = new open.furaffinity.client.pages.view(pagePath);
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
        submissionTitle.setText(page.getSubmissionTitle());
        Glide.with(this).load(page.getSubmissionImgLink()).into(submissionImage);
        Glide.with(this).load(page.getSubmissionUserIcon()).into(submissionUserIcon);
        submissionUser.setText(page.getSubmissionUser());

        fab.removeButton(submissionFavorite);
        submissionFavorite.setVisibility(View.GONE);
        if(loginTest.getIsLoggedIn()) {
            fab.addButton(submissionFavorite, 1.5f, 180);

            if(page.getIsFav()) {
                submissionFavorite.setImageResource(R.drawable.ic_menu_favorite);
            } else {
                submissionFavorite.setImageResource(R.drawable.ic_menu_unfavorite);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateUIElementListeners(View rootView) {
        submissionUserLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setUserPath(page.getSubmissionUserPage());
            }
        });

        submissionImage.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                if (page.getNext() != null) {
                    ((mainActivity) getActivity()).setViewPath(page.getNext());
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    ((mainActivity) getActivity()).setViewPath(page.getPrev());
                }
            }
        });

        submissionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(page.getDownload());

                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

                Matcher fileNameMatcher = Pattern.compile("\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)$").matcher(page.getDownload());

                if (fileNameMatcher.find()) {
                    String fileName = fileNameMatcher.group(5);

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(page.getSubmissionTitle() + " by " + page.getSubmissionUser());
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    downloadManager.enqueue(request);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("File naming error. Aborting download.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        submissionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getFavUnFav());
                            return null;
                        }
                    }.execute(webClient).get();

                    page = new open.furaffinity.client.pages.view(page.getPagePath());
                    fetchPageData();
                    checkPageLoaded();
                    updateUIElements();
                    updateUIElementListeners(rootView);
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not fav post: ", e);
                }
            }
        });
    }

    private void setupViewPager() {
        viewSectionsPagerAdapter sectionsPagerAdapter = new viewSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        getElements(rootView);
        initClientAndPage(((mainActivity) getActivity()).getViewPath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners(rootView);
        setupViewPager();
        return rootView;
    }
}