package open.furaffinity.client.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import open.furaffinity.client.R;
import open.furaffinity.client.databinding.ActivityMainBinding;
import open.furaffinity.client.databinding.ActivityMainNavigationHeaderBinding;
import open.furaffinity.client.fragments.login.LoginStatusViewModel;

public class Main extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private LoginStatusViewModel loginStatusViewModel;
    private Menu menu;
    private NavController navController;

    private void updateMenuItems(Boolean isLoggedIn) {
        if (isLoggedIn) {
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_upload).setVisible(true);
            menu.findItem(R.id.nav_msg_submission).setVisible(true);
            menu.findItem(R.id.nav_msg_others).setVisible(true);
            menu.findItem(R.id.nav_msg_pms).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_login).setVisible(false);
        }
        else {
            /*
             * Condition for finish() masks bug
             * once fixed remove the visible=false from activity_main_navigation_menu.xml
             * https://github.com/ModdingFox/OpenFuraffinityClient/issues/133
             */
            if (menu.findItem(R.id.nav_logout).isVisible()) {
                finish();
            }
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_upload).setVisible(false);
            menu.findItem(R.id.nav_msg_submission).setVisible(false);
            menu.findItem(R.id.nav_msg_others).setVisible(false);
            menu.findItem(R.id.nav_msg_pms).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(false);
            menu.findItem(R.id.nav_login).setVisible(true);
        }
    }

    private Boolean logoutMenuItem(MenuItem menuItem) {
        this.loginStatusViewModel.logout();
        return false;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.loginStatusViewModel = new ViewModelProvider(this)
            .get(LoginStatusViewModel.class);

        final ActivityMainBinding activityMainBinding =
            ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        setSupportActionBar(activityMainBinding.appBarMain.toolbar);

        final DrawerLayout drawerLayout = activityMainBinding.drawerLayout;

        this.appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_browse,
            R.id.nav_search,
            R.id.nav_upload,
            R.id.nav_profile,
            R.id.nav_msg_submission,
            R.id.nav_msg_others,
            R.id.nav_msg_pms,
            R.id.nav_history,
            R.id.nav_journal,
            R.id.nav_msg_pms_message,
            R.id.nav_user,
            R.id.nav_view,
            R.id.nav_settings,
            R.id.nav_login,
            R.id.nav_logout,
            R.id.nav_about
        ).setOpenableLayout(drawerLayout).build();
        drawerLayout.addDrawerListener(new MainDrawerListener(this.loginStatusViewModel));

        final ActivityMainNavigationHeaderBinding activityMainNavigationHeaderBinding =
            ActivityMainNavigationHeaderBinding.bind(
                activityMainBinding.navView.getHeaderView(0)
            );
        activityMainNavigationHeaderBinding.setLifecycleOwner(this);
        activityMainNavigationHeaderBinding.setLoginStatusViewModel(this.loginStatusViewModel);

        final NavigationView navigationView = activityMainBinding.navView;
        this.menu = navigationView.getMenu();
        this.loginStatusViewModel.getIsLoggedIn().observe(this, this::updateMenuItems);
        this.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener(this::logoutMenuItem);

        this.navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment_content_main
        );
        NavigationUI.setupActionBarWithNavController(
            this,
            this.navController,
            this.appBarConfiguration
        );
        NavigationUI.setupWithNavController(navigationView, this.navController);
    }

    @Override protected void onStart() {
        super.onStart();
        this.loginStatusViewModel.refreshData();
    }

    @Override public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(this.navController, this.appBarConfiguration)
            || super.onSupportNavigateUp();
    }
}
