package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.abstractPage;

public class submitMsgPmsMoveItem extends abstractPage {

    private final String pagePath;
    private final HashMap<String, String> params;
    private final String moveKey;
    private final String moveValue;

    public submitMsgPmsMoveItem(Context context, abstractPage.pageListener pageListener,
                                String pagePath, String moveKey, String moveValue,
                                HashMap<String, String> params) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.params = params;
        this.moveKey = moveKey;
        this.moveValue = moveValue;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("manage_notes", "1");
        params.put(moveKey, moveValue);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
