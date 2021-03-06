package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitControlsFoldersSubmissionsCreateGroup extends abstractPage {

    private static final String pagePath = "/controls/folders/submissions/group/add";
    private final String key;
    private final String group_name;

    public submitControlsFoldersSubmissionsCreateGroup(Context context, abstractPage.pageListener pageListener, String key, String group_name) {
        super(context, pageListener);
        this.key = key;
        this.group_name = group_name;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("position", "-1");
        params.put("group_name", group_name);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
