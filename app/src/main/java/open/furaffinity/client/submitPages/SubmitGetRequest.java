package open.furaffinity.client.submitPages;

import android.content.Context;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitGetRequest extends AbstractPage {
    private final String url;

    public SubmitGetRequest(Context context, PageListener pageListener, String url) {
        super(context, pageListener);
        this.url = url;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + url);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
