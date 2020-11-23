package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitMsgOthersDeleteAllOfType extends abstractPage {
    private final String pagePath;
    private final String paramKey;
    private final String paramValue;

    public submitMsgOthersDeleteAllOfType(Context context, abstractPage.pageListener pageListener, String pagePath, String paramKey, String paramValue) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put(paramKey, paramValue);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
