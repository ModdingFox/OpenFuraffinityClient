package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsContacts;

public class submitControlsContacts extends open.furaffinity.client.abstractClasses.abstractPage {

    private final String key;
    private final HashMap<String, String> params;

    public submitControlsContacts(Context context, abstractPage.pageListener pageListener, String key, HashMap<String, String> params) {
        super(context, pageListener);
        this.key = key;
        this.params = params;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");
        params.put("key", key);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsContacts.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
