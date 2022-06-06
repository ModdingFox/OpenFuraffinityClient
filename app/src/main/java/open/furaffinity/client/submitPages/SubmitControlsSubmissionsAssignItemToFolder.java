package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.fragmentTabs.ManageSubmissions;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsSubmissionsAssignItemToFolder extends AbstractPage {

    private final String assign_folder_id;
    private final String assign_folder_submit;
    private final HashMap<String, String> params;

    public SubmitControlsSubmissionsAssignItemToFolder(Context context,
                                                       PageListener pageListener,
                                                       String assign_folder_id,
                                                       String assign_folder_submit,
                                                       HashMap<String, String> params) {
        super(context, pageListener);
        this.assign_folder_id = assign_folder_id;
        this.assign_folder_submit = assign_folder_submit;
        this.params = params;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("assign_folder_id", assign_folder_id);
        params.put("assign_folder_submit", assign_folder_submit);
        params.putAll(this.params);

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() +
                ManageSubmissions.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
