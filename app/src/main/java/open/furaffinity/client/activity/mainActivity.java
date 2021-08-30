package open.furaffinity.client.activity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.sqlite.backDBHelper;
import open.furaffinity.client.sqlite.backContract;

import static open.furaffinity.client.utilities.messageIds.searchSelected_MESSAGE;

public class mainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu navMenu;
    private NavController navController;

    @SuppressWarnings("FieldCanBeLocal")
    private View headerView;
    private ImageView imageView;
    private TextView userName;

    private LinearLayout notificationS;
    private TextView notificationSTextView;
    private LinearLayout notificationW;
    private TextView notificationWTextView;
    private LinearLayout notificationC;
    private TextView notificationCTextView;
    private LinearLayout notificationF;
    private TextView notificationFTextView;
    private LinearLayout notificationJ;
    private TextView notificationJTextView;
    private LinearLayout notificationN;
    private TextView notificationNTextView;

    private loginCheck loginCheck;

    private String browseParamaters = null;

    private String searchSelected = null;
    private String searchParamaters = null;

    private String journalPath = null;
    private String msgPmsPath = null;
    private String userPath = null;
    private String viewPath = null;

    private backDBHelper dbHelper;

    private DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener(){
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    private void getPagePath() {
        Intent intent = getIntent();
        Uri incomingPagePath = intent.getData();

        if (intent.getStringExtra(searchSelected_MESSAGE) != null) {
            setSearchSelected(intent.getStringExtra(searchSelected_MESSAGE));
        }

        if (incomingPagePath != null) {
            String pagePath = incomingPagePath.getPath();

            Matcher pathMatcher = Pattern.compile("^/(view|user|gallery|scraps|favorites|journals|commissions|watchlist/to|watchlist/by|journal|msg/pms)/(.+)$").matcher(pagePath);

            if (pathMatcher.find()) {
                switch (pathMatcher.group(1)) {
                    case "view":
                        setViewPath(pagePath);
                        break;
                    case "user":
                    case "gallery":
                    case "scraps":
                    case "favorites":
                    case "journals":
                    case "commissions":
                    case "watchlist/to":
                    case "watchlist/by":
                        setUserPath(pagePath);
                        break;
                    case "msg/pms":
                        setMsgPmsPath(pagePath);
                        break;
                    case "journal":
                        setJournalPath(pagePath);
                        break;
                }
            }
        }
    }

    private void getElements() {
        toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navMenu = navigationView.getMenu();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.imageView);
        userName = headerView.findViewById(R.id.userName);

        notificationS = headerView.findViewById(R.id.notificationS);
        notificationSTextView = headerView.findViewById(R.id.notificationSTextView);
        notificationW = headerView.findViewById(R.id.notificationW);
        notificationWTextView = headerView.findViewById(R.id.notificationWTextView);
        notificationC = headerView.findViewById(R.id.notificationC);
        notificationCTextView = headerView.findViewById(R.id.notificationCTextView);
        notificationF = headerView.findViewById(R.id.notificationF);
        notificationFTextView = headerView.findViewById(R.id.notificationFTextView);
        notificationJ = headerView.findViewById(R.id.notificationJ);
        notificationJTextView = headerView.findViewById(R.id.notificationJTextView);
        notificationN = headerView.findViewById(R.id.notificationN);
        notificationNTextView = headerView.findViewById(R.id.notificationNTextView);
    }

    private void initClientAndPage() {
        loginCheck = new loginCheck(this, new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((loginCheck) abstractPage).getIsLoggedIn()) {
                    Glide.with(mainActivity.this).load(((loginCheck) abstractPage).getUserIcon()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading).into(imageView);
                    imageView.setOnClickListener(v -> mainActivity.this.setUserPath(((loginCheck) abstractPage).getUserPage()));

                    userName.setText(((loginCheck) abstractPage).getUserName());
                    userName.setOnClickListener(v -> mainActivity.this.setUserPath(((loginCheck) abstractPage).getUserPage()));

                    navMenu.findItem(R.id.nav_upload).setVisible(true);
                    navMenu.findItem(R.id.nav_profile).setVisible(true);
                    navMenu.findItem(R.id.nav_msg_submission).setVisible(true);
                    navMenu.findItem(R.id.nav_msg_others).setVisible(true);
                    navMenu.findItem(R.id.nav_msg_pms).setVisible(true);
                    navMenu.findItem(R.id.nav_login).setTitle(R.string.menu_logout);

                    drawer.addDrawerListener(drawerListener);

                    int notificationSCount = ((loginCheck) abstractPage).getNotificationS();
                    int notificationWCount = ((loginCheck) abstractPage).getNotificationW();
                    int notificationCCount = ((loginCheck) abstractPage).getNotificationC();
                    int notificationFCount = ((loginCheck) abstractPage).getNotificationF();
                    int notificationJCount = ((loginCheck) abstractPage).getNotificationJ();
                    int notificationNCount = ((loginCheck) abstractPage).getNotificationN();

                    if(notificationSCount > 0) {
                        notificationS.setVisibility(View.VISIBLE);
                        notificationSTextView.setText(Integer.toString(notificationSCount));
                    } else {
                        notificationS.setVisibility(View.INVISIBLE);
                    }

                    if(notificationWCount > 0) {
                        notificationW.setVisibility(View.VISIBLE);
                        notificationWTextView.setText(Integer.toString(notificationWCount));
                    } else {
                        notificationW.setVisibility(View.INVISIBLE);
                    }

                    if(notificationCCount > 0) {
                        notificationC.setVisibility(View.VISIBLE);
                        notificationCTextView.setText(Integer.toString(notificationCCount));
                    } else {
                        notificationC.setVisibility(View.INVISIBLE);
                    }

                    if(notificationFCount > 0) {
                        notificationF.setVisibility(View.VISIBLE);
                        notificationFTextView.setText(Integer.toString(notificationFCount));
                    } else {
                        notificationF.setVisibility(View.INVISIBLE);
                    }

                    if(notificationJCount > 0) {
                        notificationJ.setVisibility(View.VISIBLE);
                        notificationJTextView.setText(Integer.toString(notificationJCount));
                    } else {
                        notificationJ.setVisibility(View.INVISIBLE);
                    }

                    if(notificationNCount > 0) {
                        notificationN.setVisibility(View.VISIBLE);
                        notificationNTextView.setText(Integer.toString(notificationNCount));
                    } else {
                        notificationN.setVisibility(View.INVISIBLE);
                    }
                } else {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    userName.setText(getString(R.string.app_name));

                    navMenu.findItem(R.id.nav_upload).setVisible(false);
                    navMenu.findItem(R.id.nav_profile).setVisible(false);
                    navMenu.findItem(R.id.nav_msg_submission).setVisible(false);
                    navMenu.findItem(R.id.nav_msg_others).setVisible(false);
                    navMenu.findItem(R.id.nav_msg_pms).setVisible(false);
                    navMenu.findItem(R.id.nav_login).setTitle(R.string.menu_login);

                    drawer.removeDrawerListener(drawerListener);

                    notificationS.setVisibility(View.GONE);
                    notificationW.setVisibility(View.GONE);
                    notificationC.setVisibility(View.GONE);
                    notificationF.setVisibility(View.GONE);
                    notificationJ.setVisibility(View.GONE);
                    notificationN.setVisibility(View.GONE);
                }
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                imageView.setImageResource(R.mipmap.ic_launcher);
                userName.setText(getString(R.string.app_name));

                navMenu.findItem(R.id.nav_upload).setVisible(false);
                navMenu.findItem(R.id.nav_profile).setVisible(false);
                navMenu.findItem(R.id.nav_msg_submission).setVisible(false);
                navMenu.findItem(R.id.nav_msg_others).setVisible(false);
                navMenu.findItem(R.id.nav_msg_pms).setVisible(false);
                navMenu.findItem(R.id.nav_login).setTitle(R.string.menu_login);

                drawer.removeDrawerListener(drawerListener);

                notificationS.setVisibility(View.GONE);
                notificationW.setVisibility(View.GONE);
                notificationC.setVisibility(View.GONE);
                notificationF.setVisibility(View.GONE);
                notificationJ.setVisibility(View.GONE);
                notificationN.setVisibility(View.GONE);

                Toast.makeText(mainActivity.this, "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPageData() {
        loginCheck.execute();
    }

    public void updateUIElements() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        navMenu.findItem(R.id.nav_history).setVisible(sharedPref.getBoolean(getString(R.string.trackHistorySetting), false));
        navMenu.findItem(R.id.nav_journal).setVisible(journalPath != null);
        navMenu.findItem(R.id.nav_msg_pms_message).setVisible(msgPmsPath != null);
        navMenu.findItem(R.id.nav_user).setVisible(userPath != null);
        navMenu.findItem(R.id.nav_view).setVisible(viewPath != null);
    }

    public void updateUIElementListeners() {
        notificationS.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_submission);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_submission, 0);
        });

        notificationW.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_others);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_others, 0);
        });

        notificationC.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_others);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_others, 0);
        });

        notificationF.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_others);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_others, 0);
        });

        notificationJ.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_others);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_others, 0);
        });

        notificationN.setOnClickListener(v -> {
            navigationView.setCheckedItem(R.id.nav_msg_pms);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_pms, 0);
        });
    }

    private void setupNavigationUI() {
        setSupportActionBar(toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_browse, R.id.nav_search, R.id.nav_upload, R.id.nav_profile, R.id.nav_msg_submission, R.id.nav_msg_others, R.id.nav_msg_pms, R.id.nav_history,
                R.id.nav_journal, R.id.nav_msg_pms_message, R.id.nav_user, R.id.nav_view,
                R.id.nav_settings, R.id.nav_login, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void updateUILoginState() {
        initClientAndPage();
        fetchPageData();
        updateUIElements();

        navigationView.setCheckedItem(R.id.nav_browse);
        navigationView.getMenu().performIdentifierAction(R.id.nav_browse, 0);
    }

    public String getBrowseParamaters() {
        String result = browseParamaters;
        browseParamaters = null;
        return result;
    }

    public void setBrowseParamaters(String browseParamatersIn) {
        browseParamaters = browseParamatersIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_browse);
        navigationView.getMenu().performIdentifierAction(R.id.nav_browse, 0);
    }

    public String getSearchSelected() {
        String result = searchSelected;
        searchSelected = null;
        return result;
    }

    public void setSearchSelected(String searchSelectedIn) {
        searchSelected = searchSelectedIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_search);
        navigationView.getMenu().performIdentifierAction(R.id.nav_search, 0);
    }

    public String getSearchParamaters() {
        String result = searchParamaters;
        searchParamaters = null;
        return result;
    }

    public void setSearchParamaters(String searchParamatersIn) {
        searchParamaters = searchParamatersIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_search);
        navigationView.getMenu().performIdentifierAction(R.id.nav_search, 0);
    }

    public String getJournalPath() {
        return journalPath;
    }

    public void setJournalPath(String pathIn) {
        journalPath = pathIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_journal);
        navigationView.getMenu().performIdentifierAction(R.id.nav_journal, 0);
    }

    public String getMsgPmsPath() {
        return msgPmsPath;
    }

    public void setMsgPmsPath(String pathIn) {
        msgPmsPath = pathIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_msg_pms_message);
        navigationView.getMenu().performIdentifierAction(R.id.nav_msg_pms_message, 0);
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String pathIn) {
        userPath = pathIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_user);
        navigationView.getMenu().performIdentifierAction(R.id.nav_user, 0);
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String pathIn) {
        viewPath = pathIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_view);
        navigationView.getMenu().performIdentifierAction(R.id.nav_view, 0);
    }

    public void drawerFragmentPush(String fragmentClass, String fragmentData) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(this.getString(R.string.trackBackHistorySetting), settings.trackBackHistoryDefault)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //Delete previous versions from history
            String selection = backContract.backItemEntry.COLUMN_NAME_FRAGMENT_NAME + " LIKE ? AND " + backContract.backItemEntry.COLUMN_NAME_FRAGMENT_DATA + " LIKE ?";
            String[] selectionArgs = { fragmentClass, fragmentData};
            db.delete(backContract.backItemEntry.TABLE_NAME_BACK_HISTORY, selection , selectionArgs);

            //Insert into history
            ContentValues values = new ContentValues();
            values.put(backContract.backItemEntry.COLUMN_NAME_FRAGMENT_NAME, fragmentClass);
            values.put(backContract.backItemEntry.COLUMN_NAME_FRAGMENT_DATA, fragmentData);
            values.put(backContract.backItemEntry.COLUMN_NAME_DATETIME, (new Date()).getTime());
            db.insert(backContract.backItemEntry.TABLE_NAME_BACK_HISTORY, null, values);

            //Limit history to 512 entries
            db.execSQL("DELETE FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + " WHERE rowid < (SELECT min(rowid) FROM (SELECT rowid FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + " ORDER BY rowid DESC LIMIT 512))");

            db.close();
        }
    }

    public void drawerFragmentPop() {
        String fragmentClass = "";
        String fragmentData = "";

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(this.getString(R.string.trackBackHistorySetting), settings.trackBackHistoryDefault)) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    backContract.backItemEntry.COLUMN_NAME_FRAGMENT_NAME,
                    backContract.backItemEntry.COLUMN_NAME_FRAGMENT_DATA,
                    backContract.backItemEntry.COLUMN_NAME_DATETIME
            };

            String sortOrder = "rowid DESC";

            Cursor cursor = db.query(
                    backContract.backItemEntry.TABLE_NAME_BACK_HISTORY,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            //Remove the current page from the back history as we want to leave it
            db.execSQL("DELETE FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + " WHERE rowid = (SELECT max(rowid) FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + ")");

            if (cursor.moveToNext()) {
                fragmentClass = cursor.getString(cursor.getColumnIndexOrThrow(backContract.backItemEntry.COLUMN_NAME_FRAGMENT_NAME));
                fragmentData = cursor.getString(cursor.getColumnIndexOrThrow(backContract.backItemEntry.COLUMN_NAME_FRAGMENT_DATA));
            }

            //Remove the previous page from the back history as we will be going back to it and dont wanna get stuck in an infinite loop of returning to the same page we are going back to
            db.execSQL("DELETE FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + " WHERE rowid = (SELECT max(rowid) FROM " + backContract.backItemEntry.TABLE_NAME_BACK_HISTORY + ")");
        }

        if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.about.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_about);
            navigationView.getMenu().performIdentifierAction(R.id.nav_about, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.browse.class.getName())) {
            setBrowseParamaters(fragmentData);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.history.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_history);
            navigationView.getMenu().performIdentifierAction(R.id.nav_history, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.journal.class.getName())) {
            setJournalPath(fragmentData);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.msgOthers.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_msg_others);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_others, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.msgPms.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_msg_pms);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_pms, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.msgSubmission.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_msg_submission);
            navigationView.getMenu().performIdentifierAction(R.id.nav_msg_submission, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentTabs.msgPmsMessage.class.getName())) {
            setMsgPmsPath(fragmentData);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.profile.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_profile);
            navigationView.getMenu().performIdentifierAction(R.id.nav_profile, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.settings.class.getName())) {
            navigationView.setCheckedItem(R.id.nav_settings);
            navigationView.getMenu().performIdentifierAction(R.id.nav_settings, 0);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.search.class.getName())) {
            setSearchParamaters(fragmentData);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.user.class.getName())) {
            setUserPath(fragmentData);
        } else if (fragmentClass.equals(open.furaffinity.client.fragmentDrawers.view.class.getName())) {
            setViewPath(fragmentData);
        } else {
            this.finish();
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new backDBHelper(this);

        setContentView(R.layout.activity_main);
        System.setProperty("http.agent", "OpenFurAffinityClient");

        getElements();
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners();
        setupNavigationUI();
        getPagePath();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("searchSelected", searchSelected);
        outState.putString("searchParamaters", searchParamaters);
        outState.putString("journalPath", journalPath);
        outState.putString("msgPmsPath", msgPmsPath);
        outState.putString("userPath", userPath);
        outState.putString("viewPath", viewPath);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        searchSelected = savedInstanceState.getString("searchSelected");
        searchParamaters = savedInstanceState.getString("searchParamaters");
        journalPath = savedInstanceState.getString("journalPath");
        msgPmsPath = savedInstanceState.getString("msgPmsPath");
        userPath = savedInstanceState.getString("userPath");
        viewPath = savedInstanceState.getString("viewPath");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
