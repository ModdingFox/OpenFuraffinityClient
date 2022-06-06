package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.abstractPage;

public class submitControlsFoldersSubmissionsDeleteItem extends abstractPage {

    private final String pagePath;
    private final String key;
    private final String idName;
    private final String id;

    public submitControlsFoldersSubmissionsDeleteItem(Context context,
                                                      abstractPage.pageListener pageListener,
                                                      String pagePath, String key, String idName,
                                                      String id) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.key = key;
        this.idName = idName;
        this.id = id;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put(idName, id);

        String html = webClient.sendPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
