package open.furaffinity.client.submitPages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.Html;
import open.furaffinity.client.utilities.WebClient;

public class SubmitSubmissionPart2 extends AbstractPage {
    private static final String pagePath = "/submit/upload/";

    private final String sourceFilePath;
    private final String thumbnailFilePath;
    private final SubmitSubmissionPart1 submitSubmissionPart1;
    private String submissionKey = "";
    private final HashMap<String, String> rating = new HashMap<>();
    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> aType = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();

    public SubmitSubmissionPart2(Context context, PageListener pageListener,
                                 SubmitSubmissionPart1 submitSubmissionPart1,
                                 String sourceFilePath, String thumbnailFilePath) {
        super(context, pageListener);
        this.submitSubmissionPart1 = submitSubmissionPart1;
        this.sourceFilePath = sourceFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override protected Boolean processPageData(String html) {
        boolean result = true;
        Document doc = Jsoup.parse(html);

        Element finalizeForm = doc.selectFirst("form[id=myform]");
        if (finalizeForm != null) {
            Element keyInput = finalizeForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                submissionKey = keyInput.attr("value");
            }
            else {
                result = false;
            }
        }
        else {
            result = false;
        }

        Element catSelect = doc.selectFirst("select[name=cat]");
        Element atypeSelect = doc.selectFirst("select[name=atype]");
        Element speciesSelect = doc.selectFirst("select[name=species]");
        Element genderSelect = doc.selectFirst("select[name=gender]");
        Elements ratingInputs = doc.select("input[name=rating]");

        cat = Html.getDropDownOptions(catSelect);
        aType = Html.getDropDownOptions(atypeSelect);
        species = Html.getDropDownOptions(speciesSelect);
        gender = Html.getDropDownOptions(genderSelect);

        if (ratingInputs != null) {
            for (Element ratingInput : ratingInputs) {
                Element nextDiv = ratingInput.nextElementSibling();
                if (nextDiv != null) {
                    rating.put(ratingInput.attr("value"), nextDiv.text());
                }
            }
        }

        return result;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> simplePostParams = new HashMap<>();
        simplePostParams.put("MAX_FILE_SIZE", submitSubmissionPart1.getMaxFileSize());
        simplePostParams.put("key", submitSubmissionPart1.getSubmissionKey());
        simplePostParams.put("submission_type", submitSubmissionPart1.getSubmissionTypeCurrent());

        List<HashMap<String, String>> postParams = new ArrayList<>();

        for (String key : simplePostParams.keySet()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", key);
            newParam.put("value", simplePostParams.get(key));
            postParams.add(newParam);
        }

        if (sourceFilePath != null && !sourceFilePath.isEmpty()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "submission");
            newParam.put("filePath", sourceFilePath);
            postParams.add(newParam);
        }

        if (thumbnailFilePath != null && !thumbnailFilePath.isEmpty()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "thumbnail");
            newParam.put("filePath", thumbnailFilePath);
            postParams.add(newParam);
        }
        else {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "thumbnail");
            newParam.put("filePath", "");
            postParams.add(newParam);
        }

        String html = webClient.sendFormPostRequest(
            WebClient.getBaseUrl() + pagePath, postParams);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getSubmissionKey() {
        return submissionKey;
    }

    public HashMap<String, String> getCat() {
        return cat;
    }

    public HashMap<String, String> getaType() {
        return aType;
    }

    public HashMap<String, String> getSpecies() {
        return species;
    }

    public HashMap<String, String> getGender() {
        return gender;
    }

    public HashMap<String, String> getRating() {
        return rating;
    }
}
