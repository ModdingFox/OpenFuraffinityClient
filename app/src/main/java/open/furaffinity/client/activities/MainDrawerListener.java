package open.furaffinity.client.activities;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import open.furaffinity.client.fragments.login.LoginStatusViewModel;

public class MainDrawerListener implements DrawerLayout.DrawerListener {
    private final LoginStatusViewModel loginStatusViewModel;

    public MainDrawerListener(LoginStatusViewModel loginStatusViewModel) {
        this.loginStatusViewModel = new LoginStatusViewModel(loginStatusViewModel);
    }

    @Override public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override public void onDrawerOpened(@NonNull View drawerView) {
        this.loginStatusViewModel.refreshData();
    }

    @Override public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override public void onDrawerStateChanged(int newState) {

    }
}
