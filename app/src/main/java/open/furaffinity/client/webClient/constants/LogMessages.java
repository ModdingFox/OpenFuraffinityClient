package open.furaffinity.client.webClient.constants;

public final class LogMessages {
    public static final String NO_PREFERENCE_WITH_NAME = "No preference with name %1s";
    public static final String SAVED_COOKIE_WITH_NAME = "Saved cookie with name %1s";
    public static final String IGNORE_SET_COOKIE =
        "Ignoring set-cookie %1s as its not in the allow list";
    public static final String IS_NULL = "%1s is null";
    public static final String NO_RESPONSE_BODY = "Did not get a response body";
    public static final String HTTP_RESPONSE_FAILED = "HTTP response failed";
    public static final String RETRYABLE_REQUEST_FAILED = "Retryable HTTP request failed";
    public static final String RETRYABLE_REQUEST_LIMIT_HIT =
        "Request hit retry limit without success";
    public static final String REQUEST_INTERRUPTED = "Request was interrupted";
    public static final String HTTP_REQUEST_FAILED = "HTTP HTTP request failed";
    public static final String REQUEST_NO_DOCUMENT = "Request returned no document";

    private LogMessages() {
    }
}
