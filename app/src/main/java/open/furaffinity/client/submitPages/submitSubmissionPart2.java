package open.furaffinity.client.submitPages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;

public class submitSubmissionPart2 extends open.furaffinity.client.abstractClasses.abstractPage {
    private static final String pagePath = "/submit/upload/";

    private final String sourceFilePath;
    private final String thumbnailFilePath;

    private String submissionKey = "";
    private HashMap<String, String> rating = new HashMap<>();
    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> aType = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();

    private final open.furaffinity.client.submitPages.submitSubmissionPart1 submitSubmissionPart1;

    public submitSubmissionPart2(Context context, abstractPage.pageListener pageListener, open.furaffinity.client.submitPages.submitSubmissionPart1 submitSubmissionPart1, String sourceFilePath, String thumbnailFilePath) {
        super(context, pageListener);
        this.submitSubmissionPart1 = submitSubmissionPart1;
        this.sourceFilePath = sourceFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected Boolean processPageData(String html) {
        boolean result = true;
        Document doc = Jsoup.parse(html);

        Element finalizeForm = doc.selectFirst("form[id=myform]");
        if (finalizeForm != null) {
            Element keyInput = finalizeForm.selectFirst("input[name=key]");
            if (keyInput != null) {
                submissionKey = keyInput.attr("value");
            } else {
                result = false;
            }
        } else {
            result = false;
        }

        Element catSelect = doc.selectFirst("select[name=cat]");
        Element atypeSelect = doc.selectFirst("select[name=atype]");
        Element speciesSelect = doc.selectFirst("select[name=species]");
        Element genderSelect = doc.selectFirst("select[name=gender]");
        Elements ratingInputs = doc.select("input[name=rating]");

        cat = open.furaffinity.client.utilities.html.getDropDownOptions(catSelect);
        aType = open.furaffinity.client.utilities.html.getDropDownOptions(atypeSelect);
        species = open.furaffinity.client.utilities.html.getDropDownOptions(speciesSelect);
        gender = open.furaffinity.client.utilities.html.getDropDownOptions(genderSelect);

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

    @Override
    protected Boolean doInBackground(Void... voids) {
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

        File sourceFile = new File(sourceFilePath);
        if (sourceFile.exists()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "submission");
            newParam.put("filePath", sourceFile.getPath());
            postParams.add(newParam);
        }

        File thumbnailFile = new File(thumbnailFilePath);
        if (thumbnailFile.exists()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "thumbnail");
            newParam.put("filePath", thumbnailFile.getPath());
            postParams.add(newParam);
        } else {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "thumbnail");
            newParam.put("filePath", "");
            postParams.add(newParam);
        }

        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, postParams);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getSubmissionKey() { return submissionKey; }

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
