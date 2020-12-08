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

public class submitSubmissionPart3 extends open.furaffinity.client.abstractClasses.abstractPage {
    private static final String pagePath = "/submit/";
    private final HashMap<String, String> rating = new HashMap<>();
    private final String sourceFilePath;
    private final String thumbnailFilePath;
    private final open.furaffinity.client.submitPages.submitSubmissionPart2 submitSubmissionPart2;
    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> aType = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();

    public submitSubmissionPart3(Context context, abstractPage.pageListener pageListener, open.furaffinity.client.submitPages.submitSubmissionPart2 submitSubmissionPart2, String sourceFilePath, String thumbnailFilePath) {
        super(context, pageListener);
        this.submitSubmissionPart2 = submitSubmissionPart2;
        this.sourceFilePath = sourceFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public static String getPagePath() {
        return pagePath;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

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

        Element myformForm = doc.selectFirst("form[name=myform]");

        if (myformForm != null) {
            params = new HashMap<>();

            Elements hiddenInputs = myformForm.select("input[type=hidden]");

            if (hiddenInputs != null) {
                for (Element hiddenInput : hiddenInputs) {
                    params.put(hiddenInput.attr("name"), hiddenInput.attr("value"));
                }

                return true;
            }
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<HashMap<String, String>> postParams = new ArrayList<>();

        for (String key : submitSubmissionPart2.getParams().keySet()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", key);
            newParam.put("value", submitSubmissionPart2.getParams().get(key));
            postParams.add(newParam);
        }

        File sourceFile = new File(sourceFilePath);
        File thumbnailFile = new File(thumbnailFilePath);

        if (sourceFile.exists()) {
            HashMap<String, String> newParam = new HashMap<>();
            newParam.put("name", "submission");
            newParam.put("filePath", sourceFile.getPath());
            postParams.add(newParam);
        }

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

    public HashMap<String, String> getParams() {
        return params;
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
