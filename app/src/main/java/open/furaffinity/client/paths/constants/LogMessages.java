package open.furaffinity.client.paths.constants;

public final class LogMessages {
    public static final String LOGIN_FAILED_NO_COOKIE_SET = "Login failed(No cookies where set)";
    public static final String LOGIN_FAILED_MISSING_COOKIES =
        "Login failed(Missing Cookies) - Expected: %1s - Got: %2s";
    public static final String DOCUMENT_NULL = "Got no document back";
    public static final String LOGIN_SUCCESS = "Login Success";
    public static final String NOTIFICATION_KEY_MISSING = "Notification key missing";
    public static final String NOTIFICATION_VALUE_MISSING = "Notification value missing";
    public static final String NOTIFICATION_VALUE_NOT_A_NUMBER = "Notification value not a number";

    private LogMessages() {
    }
}
