package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitMsgSubmissionsDeleteSelected extends abstractPage {
    private final String pagePath;
    private final HashMap<String, String> params;

    public submitMsgSubmissionsDeleteSelected(Context context, abstractPage.pageListener pageListener, String pagePath, HashMap<String, String> params) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.params = params;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("messagecenter-action", "remove_checked");
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
