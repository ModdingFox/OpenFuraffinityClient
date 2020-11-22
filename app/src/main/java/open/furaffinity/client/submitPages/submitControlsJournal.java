package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.HashMap;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsJournal;

public class submitControlsJournal extends abstractPage {

    private final String pagePath;
    private final String key;
    private final String id;
    private final String subject;
    private final String body;
    private final boolean lockComments;
    private final boolean makeFeatured;

    public submitControlsJournal(Context context, abstractPage.pageListener pageListener, String pagePath, String key, String id, String subject, String body, boolean lockComments, boolean makeFeatured) {
        super(context, pageListener);
        this.pagePath = pagePath;
        this.key = key;
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.lockComments = lockComments;
        this.makeFeatured = makeFeatured;
    }

    @Override
    protected Boolean processPageData(String html) {
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("id", id);
        params.put("do", "update");
        params.put("subject", subject);
        params.put("message", body);
        params.put("submit", "Create / Update Journal");

        if (lockComments) {
            params.put("lock_comments", "on");
        }

        if (makeFeatured) {
            params.put("make_featured", "on");
        }

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
