package open.furaffinity.client.fragments.recaptchaV2;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

class RecaptchaV2WebAppInterface {
    public static final String INTERFACE_NAME = "Android";

    private final FragmentManager fragmentManager;
    private final MutableLiveData<Boolean> isResultReady;
    private final MutableLiveData<Boolean> isViewReady;

    RecaptchaV2WebAppInterface(
        FragmentManager fragmentManager,
        MutableLiveData<Boolean> isResultReady,
        MutableLiveData<Boolean> isViewReady
    ) {
        this.fragmentManager = fragmentManager;
        this.isResultReady = isResultReady;
        this.isViewReady = isViewReady;
    }

    @JavascriptInterface public void passgRecaptchaResponse(String gRecaptchaResponse) {
        final Bundle bundle = new Bundle();
        bundle.putString(RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY, gRecaptchaResponse);
        this.fragmentManager.setFragmentResult(RecaptchaV2Fragment.RECAPTCHA_RESPONSE_KEY, bundle);
        this.isResultReady.postValue(true);
    }

    @JavascriptInterface public void setWebViewVisible() {
        this.isViewReady.postValue(true);
    }
}
