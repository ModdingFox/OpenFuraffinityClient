package open.furaffinity.client.activity;

import android.content.Intent;
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
        } else {
            navMenu.findItem(R.id.nav_msg_submission).setVisible(false);
            navMenu.findItem(R.id.nav_msg_others).setVisible(false);
            navMenu.findItem(R.id.nav_msg_pms).setVisible(false);
        }
    }

    private void setupNavigationUI() {
        setSupportActionBar(toolbar);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_browse, R.id.nav_search, R.id.nav_profile, R.id.nav_msg_submission, R.id.nav_msg_others, R.id.nav_msg_pms)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        initClientAndPage();
        fetchPageData();
        updateUIElements();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, open.furaffinity.client.activity.settingsActivity.class);
                startActivity(intent);
                return true;
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
