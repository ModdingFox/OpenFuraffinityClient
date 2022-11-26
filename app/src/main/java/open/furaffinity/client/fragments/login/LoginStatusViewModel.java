package open.furaffinity.client.fragments.login;

import android.app.Application;
import android.view.View;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import open.furaffinity.client.paths.LoginPath;
import open.furaffinity.client.paths.dataTypes.LoginStatus;

public final class LoginStatusViewModel extends AndroidViewModel {
    private static final int DISPLAY_NOTIFICATION_THRESHOLD = 0;

    private final Application application;

    private final MutableLiveData<Boolean> isLoggedIn;
    private final MutableLiveData<Boolean> isNsfwAllowed;
    private final MutableLiveData<String> userIcon;
    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> userPage;

    private final MutableLiveData<String> notificationS;
    private final MutableLiveData<String> notificationW;
    private final MutableLiveData<String> notificationC;
    private final MutableLiveData<String> notificationF;
    private final MutableLiveData<String> notificationJ;
    private final MutableLiveData<String> notificationN;

    private final MutableLiveData<Integer> notificationVisibilityS;
    private final MutableLiveData<Integer> notificationVisibilityW;
    private final MutableLiveData<Integer> notificationVisibilityC;
    private final MutableLiveData<Integer> notificationVisibilityF;
    private final MutableLiveData<Integer> notificationVisibilityJ;
    private final MutableLiveData<Integer> notificationVisibilityN;

    public LoginStatusViewModel(Application application) {
        super(application);
        this.application = application;

        this.isLoggedIn = new MutableLiveData<>();
        this.isNsfwAllowed = new MutableLiveData<>();
        this.userIcon = new MutableLiveData<>();
        this.userName = new MutableLiveData<>();
        this.userPage = new MutableLiveData<>();

        this.notificationS = new MutableLiveData<>();
        this.notificationW = new MutableLiveData<>();
        this.notificationC = new MutableLiveData<>();
        this.notificationF = new MutableLiveData<>();
        this.notificationJ = new MutableLiveData<>();
        this.notificationN = new MutableLiveData<>();

        this.notificationVisibilityS = new MutableLiveData<>();
        this.notificationVisibilityW = new MutableLiveData<>();
        this.notificationVisibilityC = new MutableLiveData<>();
        this.notificationVisibilityF = new MutableLiveData<>();
        this.notificationVisibilityJ = new MutableLiveData<>();
        this.notificationVisibilityN = new MutableLiveData<>();
    }

    public LoginStatusViewModel(LoginStatusViewModel loginStatusViewModel) {
        super(loginStatusViewModel.application);
        this.application = loginStatusViewModel.application;

        this.isLoggedIn = loginStatusViewModel.isLoggedIn;
        this.isNsfwAllowed = loginStatusViewModel.isNsfwAllowed;
        this.userIcon = loginStatusViewModel.userIcon;
        this.userName = loginStatusViewModel.userName;
        this.userPage = loginStatusViewModel.userPage;

        this.notificationS = loginStatusViewModel.notificationS;
        this.notificationW = loginStatusViewModel.notificationW;
        this.notificationC = loginStatusViewModel.notificationC;
        this.notificationF = loginStatusViewModel.notificationF;
        this.notificationJ = loginStatusViewModel.notificationJ;
        this.notificationN = loginStatusViewModel.notificationN;

        this.notificationVisibilityS = loginStatusViewModel.notificationVisibilityS;
        this.notificationVisibilityW = loginStatusViewModel.notificationVisibilityW;
        this.notificationVisibilityC = loginStatusViewModel.notificationVisibilityC;
        this.notificationVisibilityF = loginStatusViewModel.notificationVisibilityF;
        this.notificationVisibilityJ = loginStatusViewModel.notificationVisibilityJ;
        this.notificationVisibilityN = loginStatusViewModel.notificationVisibilityN;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Boolean> getIsLoggedIn() {
        return this.isLoggedIn;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Boolean> getIsNsfwAllowed() {
        return this.isNsfwAllowed;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getUserIcon() {
        return this.userIcon;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getUserName() {
        return this.userName;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getUserPage() {
        return this.userPage;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationS() {
        return this.notificationS;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationW() {
        return this.notificationW;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationC() {
        return this.notificationC;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationF() {
        return this.notificationF;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationJ() {
        return this.notificationJ;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<String> getNotificationN() {
        return this.notificationN;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityS() {
        return this.notificationVisibilityS;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityW() {
        return this.notificationVisibilityW;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityC() {
        return this.notificationVisibilityC;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityF() {
        return this.notificationVisibilityF;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityJ() {
        return this.notificationVisibilityJ;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public LiveData<Integer> getNotificationVisibilityN() {
        return this.notificationVisibilityN;
    }

    private int determineNotificationLayoutVisibility(long value) {
        final int result;
        if (value > DISPLAY_NOTIFICATION_THRESHOLD) {
            result = View.VISIBLE;
        }
        else {
            result = View.GONE;
        }
        return result;
    }

    public void logout() {
        final LoginPath loginPath = new LoginPath(getApplication());
        loginPath.logout();
        this.refreshData();
    }

    public void refreshData() {
        final LoginPath loginPath = new LoginPath(getApplication());
        final LoginStatus loginStatus = loginPath.getLoginStatus();

        this.isLoggedIn.setValue(loginStatus.getIsLoggedIn());
        this.isNsfwAllowed.setValue(loginStatus.getIsNsfwAllowed());
        this.userIcon.setValue(loginStatus.getUserIcon());
        this.userName.setValue(loginStatus.getUserName());
        this.userPage.setValue(loginStatus.getUserPage());

        this.notificationS.setValue(Long.toString(loginStatus.getNotificationS()));
        this.notificationW.setValue(Long.toString(loginStatus.getNotificationW()));
        this.notificationC.setValue(Long.toString(loginStatus.getNotificationC()));
        this.notificationF.setValue(Long.toString(loginStatus.getNotificationF()));
        this.notificationJ.setValue(Long.toString(loginStatus.getNotificationJ()));
        this.notificationN.setValue(Long.toString(loginStatus.getNotificationN()));

        this.notificationVisibilityS.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationS())
        );
        this.notificationVisibilityW.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationW())
        );
        this.notificationVisibilityC.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationC())
        );
        this.notificationVisibilityF.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationF())
        );
        this.notificationVisibilityJ.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationJ())
        );
        this.notificationVisibilityN.setValue(
            determineNotificationLayoutVisibility(loginStatus.getNotificationN())
        );
    }
}
