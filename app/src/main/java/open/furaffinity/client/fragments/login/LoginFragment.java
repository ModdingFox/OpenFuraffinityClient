package open.furaffinity.client.fragments.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Strings;
import open.furaffinity.client.R;
import open.furaffinity.client.databinding.FragmentLoginBinding;
import open.furaffinity.client.fragments.constants.LogMessages;
import open.furaffinity.client.fragments.recaptchaV2.RecaptchaV2Fragment;
import open.furaffinity.client.paths.LoginPath;
import open.furaffinity.client.paths.dataTypes.BooleanAndMessage;
import open.furaffinity.client.webClient.constants.Paths;

public final class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getName();

    private FragmentLoginBinding fragmentLoginBinding;
    private View view;

    private LoginViewModel loginViewModel;
    private LoginStatusViewModel loginStatusViewModel;

    private LoginPath loginPath;

    private void doLogin(String gRecaptchaResponse) {
        final BooleanAndMessage booleanAndMessage = this.loginPath.doLogin(
            this.loginViewModel.getUserName(),
            this.loginViewModel.getPassword(),
            Strings.nullToEmpty(gRecaptchaResponse)
        );
        this.loginStatusViewModel.refreshData();
        final String loginMessage = booleanAndMessage.getMessage();
        if (loginMessage != null) {
            Snackbar.make(this.view, loginMessage, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onRecaptchaV2FragmentResult(String requestKey, Bundle bundle) {
        if (requestKey.equals(RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY)) {
            final String gRecaptchaResponse =
                bundle.getString(RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY);
            if (gRecaptchaResponse != null) {
                this.doLogin(gRecaptchaResponse);
            }
            else {
                Log.w(TAG,
                    String.format(
                        LogMessages.FRAGMENT_DID_NOT_RETURN_RESPONSE_FOR_PARAMATER,
                        RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY
                    )
                );
            }
        }
    }

    private void submitButton(View viewIn) {
        if (this.loginPath.isRecaptchaRequired().getValue()) {
            Navigation.findNavController(viewIn)
                .navigate(
                    R.id.action_nav_login_to_nav_recaptchav2,
                    RecaptchaV2Fragment.createBundle(Paths.LOGIN_URL)
                );
        }
        else {
            this.doLogin(null);
        }
    }

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        this.loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        this.loginStatusViewModel = new ViewModelProvider(requireActivity())
            .get(LoginStatusViewModel.class);
        this.loginPath = new LoginPath(requireActivity());

        this.fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        this.view = this.fragmentLoginBinding.getRoot();
        this.fragmentLoginBinding.setLoginViewModel(this.loginViewModel);
        this.fragmentLoginBinding.submitButton.setOnClickListener(this::submitButton);

        final FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.setFragmentResultListener(
            RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY,
            this,
            this::onRecaptchaV2FragmentResult
        );

        return view;
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        this.fragmentLoginBinding = null;
    }
}
