package open.furaffinity.client.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import open.furaffinity.client.R;
import open.furaffinity.client.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        final DrawerLayout drawer = binding.drawerLayout;
        final NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
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
            R.id.nav_about
        ).setOpenableLayout(drawer).build();

        final NavController navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment_content_main
        );

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment_content_main
        );

        return NavigationUI.navigateUp(
            navController,
            mAppBarConfiguration
        ) || super.onSupportNavigateUp();
    }
}
