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
    private String viewPath = null;

    private void getPagePath() {
        Intent intent = getIntent();
        Uri incomingPagePath = intent.getData();

        if (incomingPagePath != null) {
            String pagePath = incomingPagePath.getPath().toString();

            Matcher pathMatcher = Pattern.compile("^\\/(view)\\/(.+)$").matcher(pagePath);

            if (pathMatcher.find()) {
                switch (pathMatcher.group(1)) {
                    case "view":
                        setViewPath(pagePath);
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

        if(viewPath == null) {
            navMenu.findItem(R.id.nav_view).setVisible(false);
        } else {
            navMenu.findItem(R.id.nav_view).setVisible(true);
        }
    }

    private void setupNavigationUI() {
        setSupportActionBar(toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_browse, R.id.nav_search, R.id.nav_profile, R.id.nav_msg_submission, R.id.nav_msg_others, R.id.nav_msg_pms, R.id.nav_view, R.id.nav_login)
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

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQueryIn) {
        searchQuery = searchQueryIn;
        updateUIElements();
        navigationView.setCheckedItem(R.id.nav_search);
        navigationView.getMenu().performIdentifierAction(R.id.nav_search, 0);
    }

    public String getViewPath() { return viewPath; }
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
