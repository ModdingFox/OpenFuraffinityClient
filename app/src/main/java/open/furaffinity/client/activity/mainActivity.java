package open.furaffinity.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.webClient;

public class mainActivity extends AppCompatActivity {
    private static final String TAG = mainActivity.class.getName();
    private AppBarConfiguration mAppBarConfiguration;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu navMenu;
    private NavController navController;

    private webClient webClient;
    private loginTest loginTest;

    private String searchQuery = null;
    private String journalPath = null;
    private String msgPmsPath = null;
    private String userPath = null;
    private String viewPath = null;

    private void getPagePath() {
        Intent intent = getIntent();
        Uri incomingPagePath = intent.getData();

        if (incomingPagePath != null) {
            String pagePath = incomingPagePath.getPath().toString();

            Matcher pathMatcher = Pattern.compile("^\\/(view|user|gallery|scraps|favorites|journals|commissions|watchlist\\/to|watchlist\\/by|journal|msg\\/pms)\\/(.+)$").matcher(pagePath);

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
        navMenu.findItem(R.id.nav_profile).setVisible(false);//Hiding this until its ready

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    private void initClientAndPage() {
        webClient = new webClient(this);
        loginTest = new loginTest();
    }

    private void fetchPageData() {
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    private void updateUIElements() {
        if (loginTest.getIsLoggedIn()) {
            navMenu.findItem(R.id.nav_msg_submission).setVisible(true);
            navMenu.findItem(R.id.nav_msg_others).setVisible(true);
            navMenu.findItem(R.id.nav_msg_pms).setVisible(true);
            navMenu.findItem(R.id.nav_login).setTitle(R.string.menu_logout);
        } else {
            navMenu.findItem(R.id.nav_msg_submission).setVisible(false);
            navMenu.findItem(R.id.nav_msg_others).setVisible(false);
            navMenu.findItem(R.id.nav_msg_pms).setVisible(false);
            navMenu.findItem(R.id.nav_login).setTitle(R.string.menu_login);
        }

        if (journalPath == null) {
            navMenu.findItem(R.id.nav_journal).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_journal).setVisible(true);
        }

        if (msgPmsPath == null) {
            navMenu.findItem(R.id.nav_msg_pms_message).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_msg_pms_message).setVisible(true);
        }

        if (userPath == null) {
            navMenu.findItem(R.id.nav_user).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_user).setVisible(true);
        }

        if (viewPath == null) {
            navMenu.findItem(R.id.nav_view).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_view).setVisible(true);
        }
    }

    private void setupNavigationUI() {
        setSupportActionBar(toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_browse, R.id.nav_search, R.id.nav_profile, R.id.nav_msg_submission, R.id.nav_msg_others, R.id.nav_msg_pms, R.id.nav_history,
                R.id.nav_journal, R.id.nav_msg_pms_message, R.id.nav_user, R.id.nav_view,
                R.id.nav_login, R.id.nav_about)
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

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQueryIn) {
        searchQuery = searchQueryIn;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getElements();
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        setupNavigationUI();
        getPagePath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
