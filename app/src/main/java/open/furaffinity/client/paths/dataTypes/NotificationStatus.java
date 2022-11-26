package open.furaffinity.client.paths.dataTypes;

public final class NotificationStatus {
    private final int notificationS;
    private final int notificationW;
    private final int notificationC;
    private final int notificationF;
    private final int notificationJ;
    private final int notificationN;

    public NotificationStatus(
        int notificationS,
        int notificationW,
        int notificationC,
        int notificationF,
        int notificationJ,
        int notificationN
    ) {
        this.notificationS = notificationS;
        this.notificationW = notificationW;
        this.notificationC = notificationC;
        this.notificationF = notificationF;
        this.notificationJ = notificationJ;
        this.notificationN = notificationN;
    }

    public Integer getNotificationS() {
        return this.notificationS;
    }

    public Integer getNotificationW() {
        return this.notificationW;
    }

    public Integer getNotificationC() {
        return this.notificationC;
    }

    public Integer getNotificationF() {
        return this.notificationF;
    }

    public Integer getNotificationJ() {
        return this.notificationJ;
    }

    public Integer getNotificationN() {
        return notificationN;
    }
}
