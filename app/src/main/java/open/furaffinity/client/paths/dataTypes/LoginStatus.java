package open.furaffinity.client.paths.dataTypes;

import com.google.common.base.Strings;

public final class LoginStatus {
    private final boolean isLoggedIn;
    private final boolean isNsfwAllowed;
    private final String userIcon;
    private final String userName;
    private final String userPage;
    private final NotificationStatus notificationStatus;

    public LoginStatus(
        boolean isLoggedIn,
        boolean isNsfwAllowed,
        String userIcon,
        String userName,
        String userPage,
        NotificationStatus notificationStatus
    ) {
        this.isLoggedIn = isLoggedIn;
        this.isNsfwAllowed = isNsfwAllowed;
        this.userIcon = userIcon;
        this.userName = userName;
        this.userPage = userPage;
        this.notificationStatus = notificationStatus;
    }

    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }

    public boolean getIsNsfwAllowed() {
        return this.isNsfwAllowed;
    }

    public String getUserIcon() {
        return Strings.nullToEmpty(this.userIcon);
    }

    public String getUserName() {
        return Strings.nullToEmpty(this.userName);
    }

    public String getUserPage() {
        return Strings.nullToEmpty(this.userPage);
    }

    public Integer getNotificationS() {
        return this.notificationStatus.getNotificationS();
    }

    public Integer getNotificationW() {
        return this.notificationStatus.getNotificationW();
    }

    public Integer getNotificationC() {
        return this.notificationStatus.getNotificationC();
    }

    public Integer getNotificationF() {
        return this.notificationStatus.getNotificationF();
    }

    public Integer getNotificationJ() {
        return this.notificationStatus.getNotificationJ();
    }

    public Integer getNotificationN() {
        return this.notificationStatus.getNotificationN();
    }
}
