package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentTabs.manageSubmissions;

public class submitControlsSubmissionsAssignItemToFolder extends abstractPage {

    private final String assign_folder_id;
    private final String assign_folder_submit;
    private final HashMap<String, String> params;

    public submitControlsSubmissionsAssignItemToFolder(Context context, abstractPage.pageListener pageListener, String assign_folder_id, String assign_folder_submit, HashMap<String, String> params) {
        super(context, pageListener);
        this.assign_folder_id = assign_folder_id;
        this.assign_folder_submit = assign_folder_submit;
        this.params = params;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("assign_folder_id", assign_folder_id);
        params.put("assign_folder_submit", assign_folder_submit);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + manageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
