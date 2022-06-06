package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitMsgOthersDeleteAllOfType extends AbstractPage {
    private final String pagePath;
    private final String paramKey;
    private final String paramValue;

    public SubmitMsgOthersDeleteAllOfType(Context context, PageListener pageListener,
                                          String pagePath, String paramKey, String paramValue) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put(paramKey, paramValue);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
