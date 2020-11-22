package open.furaffinity.client.submitPages;

import android.content.Context;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitGetRequest extends open.furaffinity.client.abstractClasses.abstractPage {
    private final String url;

    public submitGetRequest(Context context, abstractPage.pageListener pageListener, String url) {
        super(context, pageListener);
        this.url = url;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + url);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
