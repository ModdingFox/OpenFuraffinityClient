package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsShouts;

public class submitShouts extends abstractPage {
    private final HashMap<String, String> params;

    public submitShouts(Context context, abstractPage.pageListener pageListener,
                        HashMap<String, String> params) {
        super(context, pageListener);
        this.params = params;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsShouts.getPagePath(),
            params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
