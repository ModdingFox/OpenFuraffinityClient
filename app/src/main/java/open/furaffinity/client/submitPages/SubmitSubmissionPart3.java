package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class SubmitSubmissionPart3 extends AbstractPage {
    private static final String pagePath = "/submit/finalize/";

    private final String key;

    private final String cat;
    private final String aType;
    private final String species;
    private final String gender;
    private final String rating;

    private final String title;
    private final String message;
    private final String keywords;

    private final Boolean disableComments;
    private final Boolean putInScraps;

    private final List<String> folderIds;
    private final String newFolderName;

    public SubmitSubmissionPart3(Context context, PageListener pageListener,
                                 String key, String cat, String aType, String species,
                                 String gender, String rating, String title, String message,
                                 String keywords, Boolean disableComments, Boolean putInScraps,
                                 List<String> folderIds, String newFolderName) {
        super(context, pageListener);
        this.key = key;
        this.cat = cat;
        this.aType = aType;
        this.species = species;
        this.gender = gender;
        this.rating = rating;
        this.title = title;
        this.message = message;
        this.keywords = keywords;
        this.disableComments = disableComments;
        this.putInScraps = putInScraps;
        this.folderIds = folderIds;
        this.newFolderName = newFolderName;
    }

    @Override protected Boolean processPageData(String html) {
        //Document doc = Jsoup.parse(html);
        return true;//Really should test this at some point.
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();

        params.put("key", key);

        params.put("atype", aType);
        params.put("species", species);
        params.put("gender", gender);
        params.put("cat", cat);
        params.put("rating", rating);

        params.put("title", title);
        params.put("message", message);
        params.put("keywords", keywords);

        for (int i = 0; i < folderIds.size(); i++) {
            params.put("folder_ids[" + i + "]", folderIds.get(i));
        }

        if (newFolderName != null && !newFolderName.isEmpty()) {
            params.put("create_folder_name", newFolderName);
        }

        if (disableComments) {
            params.put("lock_comments", "1");
        }

        if (putInScraps) {
            params.put("scrap", "1");
        }

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() + pagePath, params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
