package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsAvatar;

public class submitNewAvatar extends open.furaffinity.client.abstractClasses.abstractPage {
    private final String filePath;

    public submitNewAvatar(Context context, abstractPage.pageListener pageListener,
                           String filePath) {
        super(context, pageListener);
        this.filePath = filePath;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        List<HashMap<String, String>> params = new ArrayList<>();

        HashMap<String, String> newParam = new HashMap<>();
        newParam.put("name", "do");
        newParam.put("value", "uploadavatar");
        params.add(newParam);

        newParam = new HashMap<>();
        newParam.put("name", "avatarfile");
        newParam.put("filePath", filePath);
        params.add(newParam);

        String html = webClient.sendFormPostRequest(
            open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsAvatar.getPagePath(),
            params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
