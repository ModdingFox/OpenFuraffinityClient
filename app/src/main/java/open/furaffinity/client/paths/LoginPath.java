package open.furaffinity.client.paths;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.FormBody;
import open.furaffinity.client.paths.constants.LogMessages;
import open.furaffinity.client.paths.dataTypes.BooleanAndMessage;
import open.furaffinity.client.paths.dataTypes.LoginFormBody;
import open.furaffinity.client.paths.dataTypes.LoginStatus;
import open.furaffinity.client.paths.dataTypes.NotificationStatus;
import open.furaffinity.client.paths.dataTypes.StringKeyIntValue;
import open.furaffinity.client.webClient.constants.Paths;

public final class LoginPath extends AbstractPath {
    private static final String TAG = LoginPath.class.getName();
    private static final String CSS_SELECTOR_LOGIN_LINK = "a[href=/login]";
    private static final String CSS_SELECTOR_GRECAPTCHA = "[id=g-recaptcha]";
    private static final String CSS_SELECTOR_LOGIN_ERROR_MESSAGE = ".login-msg";
    private static final String CSS_SELECTOR_USER_ICON_IMAGE = "img.loggedin_user_avatar";
    private static final String CSS_SELECTOR_NOTIFICATION_CONTAINER = "a.notification-container";
    private static final String CSS_SELECTOR_NSFW_TOGGLE =
        "input.slider-toggle[id=sfw-toggle-mobile]";
    private static final String ITEM_DELIMITER = ",";
    private static final String NOTIFICATION_REGEX = "(\\d+)([S|W|C|F|J|N])";
    private static final String ATTRIBUTE_KEY_ALT = "alt";
    private static final String ATTRIBUTE_KEY_SRC = "src";
    private static final String ATTRIBUTE_KEY_HREF = "href";
    private static final String IMAGE_URL_MISSING_PROTOCOL_PARTS = "https:";
    private static final int DEFAULT_NOTIFICATION_COUNT = 0;

    private enum NotificationIdentifiers {
        SUBMISSION("S"),
        WATCH("W"),
        COMMENT("C"),
        FAVORITE("F"),
        JOURNAL("J"),
        NOTE("N");

        private final String identifier;

        NotificationIdentifiers(String identifier) {
            this.identifier = identifier;
        }

        @NonNull @Override public String toString() {
            return this.identifier;
        }
    }

    public LoginPath(Context context) {
        super(context);
    }

    public static BooleanAndMessage isRecaptchaRequired(Document document) {
        final BooleanAndMessage result;
        if (document != null) {
            final Element element = document.selectFirst(CSS_SELECTOR_GRECAPTCHA);
            result = new BooleanAndMessage(null, element != null);
        }
        else {
            result = new BooleanAndMessage(LogMessages.DOCUMENT_NULL, false);
        }
        return result;
    }

    public BooleanAndMessage isRecaptchaRequired() {
        return isRecaptchaRequired(this.httpConnection.sendGetRequest(Paths.LOGIN_URL));
    }

    private static String checkForLoginError(Document document) {
        String result = null;
        if (document == null) {
            result = LogMessages.DOCUMENT_NULL;
        }
        else {
            final Element element = document.selectFirst(CSS_SELECTOR_LOGIN_ERROR_MESSAGE);
            if (element != null) {
                result = element.text();
            }
        }
        return result;
    }

    public BooleanAndMessage doLogin(
        @NonNull String userName,
        @NonNull String password,
        @NonNull String gRecaptchaResponse
    ) {
        final BooleanAndMessage result;

        final LoginFormBody loginFormBody = new LoginFormBody(
            userName,
            password,
            gRecaptchaResponse
        );

        final FormBody formBody = loginFormBody.getFormBody();
        final Document document = this.httpConnection.sendPostRequest(Paths.LOGIN_URL, formBody);

        final String errorCheck = checkForLoginError(document);
        final Set<String> loginCookieNames =
            this.abstractPathSharedPreferences.getSavableLoginCookieNames();
        final List<String> currentLoginCookies = this.sharedPrefCookieJar
            .getCurrentCookieNames()
            .stream()
            .filter(loginCookieNames::contains)
            .collect(Collectors.toList());

        if (errorCheck != null) {
            Log.w(TAG, errorCheck);
            result = new BooleanAndMessage(errorCheck, false);
        }
        else if (currentLoginCookies.size() == 0) {
            Log.e(TAG, LogMessages.LOGIN_FAILED_NO_COOKIE_SET);
            result = new BooleanAndMessage(LogMessages.LOGIN_FAILED_NO_COOKIE_SET, false);
        }
        else if (currentLoginCookies.size() != loginCookieNames.size()) {
            final String expectedCookieNames = String.join(
                ITEM_DELIMITER, loginCookieNames
            );
            final String gotCookieNames = String.join(ITEM_DELIMITER, currentLoginCookies);
            final String failureMessage = String.format(
                LogMessages.LOGIN_FAILED_MISSING_COOKIES,
                expectedCookieNames,
                gotCookieNames
            );
            Log.e(TAG, failureMessage);
            result = new BooleanAndMessage(failureMessage, false);
        }
        else {
            result = new BooleanAndMessage(LogMessages.LOGIN_SUCCESS, true);
        }

        return result;
    }

    public void logout() {
        this.sharedPrefCookieJar.clearPersistCookies();
    }

    private static StringKeyIntValue notificationStringToMap(String notification) {
        StringKeyIntValue stringKeyIntValue = null;
        final Pattern pattern = Pattern.compile(NOTIFICATION_REGEX);
        final Matcher matcher = pattern.matcher(notification);
        if (matcher.matches() && matcher.groupCount() == 2) {
            final String key = matcher.group(2);
            final String value = matcher.group(1);

            if (key == null) {
                Log.e(TAG, LogMessages.NOTIFICATION_KEY_MISSING);
            }
            else if (value == null) {
                Log.e(TAG, LogMessages.NOTIFICATION_VALUE_MISSING);
            }
            else {
                try {
                    stringKeyIntValue = new StringKeyIntValue(
                        key,
                        Integer.parseInt(value)
                    );
                }
                catch (NumberFormatException ex) {
                    Log.e(TAG, LogMessages.NOTIFICATION_VALUE_NOT_A_NUMBER);
                }
            }
        }
        return stringKeyIntValue;
    }

    public LoginStatus getLoginStatus() {
        final LoginStatus loginStatus;
        final Document document = this.httpConnection.sendGetRequest(Paths.LOGIN_URL);

        if (document != null) {
            final Element userIconImg = document.selectFirst(CSS_SELECTOR_USER_ICON_IMAGE);
            final String userIconImgSrc;
            final String userIconImgAlt;
            final String userIconImgHref;

            if (userIconImg != null) {
                userIconImgSrc =
                    IMAGE_URL_MISSING_PROTOCOL_PARTS + userIconImg.attr(ATTRIBUTE_KEY_SRC);
                userIconImgAlt = userIconImg.attr(ATTRIBUTE_KEY_ALT);
                final Element userIconImgParent = userIconImg.parent();
                if (userIconImgParent != null) {
                    userIconImgHref = userIconImgParent.attr(ATTRIBUTE_KEY_HREF);
                }
                else {
                    userIconImgHref = StringUtils.EMPTY;
                }
            }
            else {
                userIconImgSrc = StringUtils.EMPTY;
                userIconImgAlt = StringUtils.EMPTY;
                userIconImgHref = StringUtils.EMPTY;
            }

            final Elements notifications = document.select(CSS_SELECTOR_NOTIFICATION_CONTAINER);
            final Map<String, Integer> notificationMap = notifications
                .stream()
                .map(Element::text)
                .distinct()
                .map(LoginPath::notificationStringToMap)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(StringKeyIntValue::getKey, StringKeyIntValue::getValue));
            final NotificationStatus notificationStatus = new NotificationStatus(
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.SUBMISSION.toString(),
                    DEFAULT_NOTIFICATION_COUNT
                )).orElse(DEFAULT_NOTIFICATION_COUNT),
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.WATCH.toString(),
                    DEFAULT_NOTIFICATION_COUNT
                )).orElse(DEFAULT_NOTIFICATION_COUNT),
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.COMMENT.toString(),
                    DEFAULT_NOTIFICATION_COUNT
                )).orElse(DEFAULT_NOTIFICATION_COUNT),
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.FAVORITE.toString(),
                    DEFAULT_NOTIFICATION_COUNT
                )).orElse(DEFAULT_NOTIFICATION_COUNT),
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.JOURNAL.toString(),
                    DEFAULT_NOTIFICATION_COUNT
                )).orElse(DEFAULT_NOTIFICATION_COUNT),
                Optional.ofNullable(notificationMap.getOrDefault(
                    NotificationIdentifiers.NOTE.toString(), DEFAULT_NOTIFICATION_COUNT)
                ).orElse(DEFAULT_NOTIFICATION_COUNT)
            );

            loginStatus = new LoginStatus(
                document.selectFirst(CSS_SELECTOR_LOGIN_LINK) == null,
                document.selectFirst(CSS_SELECTOR_NSFW_TOGGLE) != null,
                userIconImgSrc,
                userIconImgAlt,
                userIconImgHref,
                notificationStatus
            );
        }
        else {
            loginStatus = null;
        }
        return loginStatus;
    }
}
