package open.furaffinity.client.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.pages.Login;
import open.furaffinity.client.submitPages.SubmitLogin;
import open.furaffinity.client.utilities.WebClient;

public class LoginDialog extends DialogFragment {
    private EditText userName;
    private EditText password;

    private Activity activity;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private CookieManager cookieManager;

    private final AbstractPage.PageListener pageListener = new AbstractPage.PageListener() {
        @Override
        public void requestSucceeded(
            AbstractPage abstractPage) {
            cookieManager.setCookie(
                ((SubmitLogin) abstractPage).getA().domain(),
                ((SubmitLogin) abstractPage).getA().toString());
            cookieManager.setCookie(
                ((SubmitLogin) abstractPage).getB().domain(),
                ((SubmitLogin) abstractPage).getB().toString());

            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(activity.getString(R.string.webClientCookieA),
                ((SubmitLogin) abstractPage).getA().value());
            editor.putString(activity.getString(R.string.webClientCookieB),
                ((SubmitLogin) abstractPage).getB().value());
            editor.apply();
            editor.commit();

            Toast.makeText(activity, "Successful user login", Toast.LENGTH_SHORT).show();
            ((MainActivity) activity).updateUiLoginState();
        }

        @Override
        public void requestFailed(
            AbstractPage abstractPage) {
            Toast.makeText(activity, "Failed to login user", Toast.LENGTH_SHORT).show();
            ((MainActivity) activity).updateUiLoginState();
        }
    };

    protected int getLayout() {
        return R.layout.dialog_fragment_login;
    }

    protected void getElements(View rootView) {
        userName = rootView.findViewById(R.id.userName);
        password = rootView.findViewById(R.id.password);
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(getLayout(), null);
        getElements(rootView);

        activity = requireActivity();
        fragmentManager = getParentFragmentManager();
        sharedPref =
            activity.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
        cookieManager = CookieManager.getInstance();

        if (sharedPref.getString(activity.getString(R.string.webClientCookieA), null) != null
            || sharedPref.getString(activity.getString(R.string.webClientCookieB), null) != null) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();

            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(getString(R.string.webClientCookieA));
            editor.remove(getString(R.string.webClientCookieB));
            editor.apply();
            editor.commit();

            Toast.makeText(getActivity(), "Logged out user", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).updateUiLoginState();
        }

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> {
                new Login(activity, new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            if (((Login) abstractPage).isRecaptchaRequired()) {
                                final RecaptchaV2Dialog recaptchaV2Dialog = new RecaptchaV2Dialog();
                                recaptchaV2Dialog.setPagePath(
                                    WebClient.getBaseUrl() + Login.getPagePath());

                                recaptchaV2Dialog.setListener(
                                    gRecaptchaResponse -> {
                                        new SubmitLogin(
                                            activity,
                                            pageListener,
                                            userName.getText().toString(),
                                            password.getText().toString(),
                                            gRecaptchaResponse).execute();
                                    });
                                recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
                            }
                            else {
                                new SubmitLogin(
                                    activity,
                                    pageListener,
                                    userName.getText().toString(),
                                    password.getText().toString(),
                                    "").execute();
                            }
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(activity, "Failed to determine if reCaptcha is needed",
                                Toast.LENGTH_SHORT).show();
                        }
                    }).execute();
            });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }
}
