package open.furaffinity.client.submitPages;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitSubmissionPart4 extends abstractPage {
    private final List<HashMap<String, String>> params;

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

    public submitSubmissionPart4(Context context, abstractPage.pageListener pageListener, List<HashMap<String, String>> params, String cat, String aType, String species, String gender, String rating, String title, String message, String keywords, Boolean disableComments, Boolean putInScraps) {
        super(context, pageListener);
        this.params = params;
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
    }

    @Override
    protected Boolean processPageData(String html) {
        //Document doc = Jsoup.parse(html);
        return true;//Really should test this at some point.
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<HashMap<String, String>> params = new ArrayList<>(this.params);

        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("cat", cat));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("atype", aType));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("species", species));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("gender", gender));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("rating", rating));

        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("title", title));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("message", message));
        params.add(open.furaffinity.client.utilities.webClient.nameValueToHashMap("keywords", keywords));

        if (disableComments) {
            HashMap<String, String> disableCommentsHashMap = new HashMap<>();
            disableCommentsHashMap.put("name", "lock_comments");
            disableCommentsHashMap.put("value", "on");
            params.add(disableCommentsHashMap);
        }

        if (putInScraps) {
            HashMap<String, String> putInScrapsHashMap = new HashMap<>();
            putInScrapsHashMap.put("name", "scrap");
            putInScrapsHashMap.put("value", "1");
            params.add(putInScrapsHashMap);
        }

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + submitSubmissionPart3.getPagePath(), params, false);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
