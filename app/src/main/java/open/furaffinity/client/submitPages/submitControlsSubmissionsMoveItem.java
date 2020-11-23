package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentTabs.manageSubmissions;

public class submitControlsSubmissionsMoveItem extends abstractPage {

    private final HashMap<String, String> params;
    private final String moveKey;
    private final String moveValue;

    public submitControlsSubmissionsMoveItem(Context context, abstractPage.pageListener pageListener, String moveKey, String moveValue, HashMap<String, String> params) {
        super(context, pageListener);
        this.params = params;
        this.moveKey = moveKey;
        this.moveValue = moveValue;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put(moveKey, moveValue);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + manageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
