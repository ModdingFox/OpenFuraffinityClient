package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.abstractPage;

public class submitShout extends abstractPage {
    private final String pagePath;
    private final String key;
    private final String name;
    private final String shout;

    public submitShout(Context context, abstractPage.pageListener pageListener, String pagePath,
                       String key, String name, String shout) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.key = key;
        this.name = name;
        this.shout = shout;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "shout");
        params.put("key", key);
        params.put("name", name);
        params.put("shout", shout);
        params.put("submit", "Submit");

        String html = webClient.sendPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
