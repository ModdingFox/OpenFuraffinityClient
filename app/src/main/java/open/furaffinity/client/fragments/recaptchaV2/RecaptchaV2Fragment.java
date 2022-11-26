package open.furaffinity.client.fragments.recaptchaV2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import open.furaffinity.client.databinding.FragmentRecaptchaV2Binding;
import open.furaffinity.client.fragments.constants.LogMessages;

public final class RecaptchaV2Fragment extends Fragment {
    public static final String RECAPTCHA_RESPONSE_KEY = "recaptchaResponse";

    private static final String TAG = RecaptchaV2Fragment.class.getName();
    private static final String ARGUMENT_PAGE_PATH = "pagePath";

    private FragmentRecaptchaV2Binding fragmentRecaptchaV2Binding;

    public static Bundle createBundle(@NonNull String pagePath) {
        final Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_PAGE_PATH, pagePath);
        return bundle;
    }

    private String getArgumentPagePath(View view) {
        final Bundle bundle = getArguments();
        String pagePath = null;
        if (bundle != null) {
            pagePath = bundle.getString(ARGUMENT_PAGE_PATH);
        }
        if (pagePath == null) {
            Log.e(TAG,
                String.format(
                    LogMessages.BUNDLE_MISSING_REQUIRED_ARGUMENT_OF_TYPE,
                    ARGUMENT_PAGE_PATH,
                    String.class.getName()
                )
            );
            Navigation.findNavController(view).navigateUp();
        }
        return pagePath;
    }

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentRecaptchaV2Binding = FragmentRecaptchaV2Binding.inflate(inflater, container, false);
        return fragmentRecaptchaV2Binding.getRoot();
    }

    /*
     * Suppress js enabled warn
     * This trick needs to mess with the dom to make things look more integrated even if its not
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setVisibility(View.INVISIBLE);

        final MutableLiveData<Boolean> isResultReady = new MutableLiveData<>(false);
        final MutableLiveData<Boolean> isViewReady = new MutableLiveData<>(false);

        isViewReady.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                view.setVisibility(View.VISIBLE);
            }
        });

        isResultReady.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        final RecaptchaV2WebAppInterface recaptchaV2WebAppInterface =
            new RecaptchaV2WebAppInterface(getParentFragmentManager(), isResultReady, isViewReady);

        final WebSettings webSettings = fragmentRecaptchaV2Binding
            .layoutTemplateWebView
            .webView
            .getSettings();
        webSettings.setJavaScriptEnabled(true);

        fragmentRecaptchaV2Binding
            .layoutTemplateWebView
            .webView
            .setBackgroundColor(Color.TRANSPARENT);
        fragmentRecaptchaV2Binding.layoutTemplateWebView.webView.addJavascriptInterface(
            recaptchaV2WebAppInterface,
            RecaptchaV2WebAppInterface.INTERFACE_NAME
        );

        fragmentRecaptchaV2Binding.layoutTemplateWebView.webView.loadUrl(getArgumentPagePath(view));
        fragmentRecaptchaV2Binding.layoutTemplateWebView.webView.setWebViewClient(
            new RecaptchaV2WebViewClient()
        );
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentRecaptchaV2Binding = null;
    }
}
