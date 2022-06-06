package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentTabs.manageSubmissions;

public class submitControlsSubmissionsAssignItemToNewFolder extends abstractPage {

    private final String create_folder_name;
    private final String create_folder_submit;
    private final HashMap<String, String> params;

    public submitControlsSubmissionsAssignItemToNewFolder(Context context,
                                                          abstractPage.pageListener pageListener,
                                                          String create_folder_name,
                                                          String create_folder_submit,
                                                          HashMap<String, String> params) {
        super(context, pageListener);
        this.create_folder_name = create_folder_name;
        this.create_folder_submit = create_folder_submit;
        this.params = params;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("create_folder_name", create_folder_name);
        params.put("create_folder_submit", create_folder_submit);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() +
                manageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
