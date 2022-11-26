package open.furaffinity.client.paths.dataTypes;

import androidx.annotation.NonNull;
import okhttp3.FormBody;

public final class LoginFormBody {
    private static final String LOGIN_KEY_ACTION = "action";
    private static final String LOGIN_KEY_NAME = "name";
    private static final String LOGIN_KEY_PASS = "pass";
    private static final String LOGIN_KEY_GRECAPTCHA = "g-recaptcha-response";
    private static final String LOGIN_VALUE_ACTION = "login";

    private final FormBody.Builder builder;

    public LoginFormBody(
        @NonNull String userName,
        @NonNull String password,
        @NonNull String gRecaptchaResponse
    ) {
        this.builder = new FormBody.Builder()
            .add(LOGIN_KEY_ACTION, LOGIN_VALUE_ACTION)
            .add(LOGIN_KEY_NAME, userName)
            .add(LOGIN_KEY_PASS, password)
            .add(LOGIN_KEY_GRECAPTCHA, gRecaptchaResponse);
    }

    public FormBody getFormBody() {
        return builder.build();
    }

}
