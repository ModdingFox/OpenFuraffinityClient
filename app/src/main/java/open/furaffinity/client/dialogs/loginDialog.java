package open.furaffinity.client.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;

public class loginDialog extends DialogFragment {
    private EditText userName;
    private EditText password;

    private Activity activity;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private CookieManager cookieManager;

    private final abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override
        public void requestSucceeded(
            open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
            cookieManager.setCookie(
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getA().domain(),
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getA().toString());
            cookieManager.setCookie(
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getB().domain(),
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getB().toString());

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(activity.getString(R.string.webClientCookieA),
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getA().value());
            editor.putString(activity.getString(R.string.webClientCookieB),
                ((open.furaffinity.client.submitPages.submitLogin) abstractPage).getB().value());
            editor.apply();
            editor.commit();

            Toast.makeText(activity, "Successful user login", Toast.LENGTH_SHORT).show();
            ((mainActivity) activity).updateUILoginState();
        }

        @Override
        public void requestFailed(
            open.furaffinity.client.abstractClasses.abstractPage abstractPage) {
            Toast.makeText(activity, "Failed to login user", Toast.LENGTH_SHORT).show();
            ((mainActivity) activity).updateUILoginState();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(getLayout(), null);
        getElements(rootView);

        activity = requireActivity();
        fragmentManager = getParentFragmentManager();
        sharedPref =
            activity.getSharedPreferences(getString(R.string.settingsFile), Context.MODE_PRIVATE);
        cookieManager = CookieManager.getInstance();

        if (sharedPref.getString(activity.getString(R.string.webClientCookieA), null) != null ||
            sharedPref.getString(activity.getString(R.string.webClientCookieB), null) != null) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(getString(R.string.webClientCookieA));
            editor.remove(getString(R.string.webClientCookieB));
            editor.apply();
            editor.commit();

            Toast.makeText(getActivity(), "Logged out user", Toast.LENGTH_SHORT).show();
            ((mainActivity) requireActivity()).updateUILoginState();
        }

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> new open.furaffinity.client.pages.login(activity,
                new abstractPage.pageListener() {
                    @Override public void requestSucceeded(abstractPage abstractPage) {
                        if (((open.furaffinity.client.pages.login) abstractPage).isRecaptchaRequired()) {
                            recaptchaV2Dialog recaptchaV2Dialog = new recaptchaV2Dialog();
                            recaptchaV2Dialog.setPagePath(
                                open.furaffinity.client.utilities.webClient.getBaseUrl() +
                                    open.furaffinity.client.pages.login.getPagePath());

                            recaptchaV2Dialog.setListener(
                                gRecaptchaResponse -> new open.furaffinity.client.submitPages.submitLogin(
                                    activity, pageListener, userName.getText().toString(),
                                    password.getText().toString(), gRecaptchaResponse).execute());
                            recaptchaV2Dialog.show(fragmentManager, "recaptchaV2");
                        }
                        else {
                            new open.furaffinity.client.submitPages.submitLogin(activity,
                                pageListener, userName.getText().toString(),
                                password.getText().toString(), "").execute();
                        }
                    }

                    @Override public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(activity, "Failed to determine if reCaptcha is needed",
                            Toast.LENGTH_SHORT).show();
                    }
                }).execute());
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {

        });

        return builder.create();
    }
}
