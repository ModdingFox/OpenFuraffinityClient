package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.pages.controlsProfile;

public class submitControlsProfile extends BasePage {
    private final String key;
    private final HashMap<String, String> params;

    public submitControlsProfile(Context context, BasePage.pageListener pageListener, String key, HashMap<String, String> params) {
        super(context, pageListener);
        this.key = key;
        this.params = params;
    }

    @Override
    protected Boolean processPageData(String html) {
        //for this page adding validation is really not reasonable at the moment as even in error it just comes back to the original page.
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.put("key", key);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsProfile.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
