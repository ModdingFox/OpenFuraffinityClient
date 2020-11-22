package open.furaffinity.client.submitPageOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class submitSubmissionPart3 extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = submitSubmissionPart3.class.getName();

    private static String pagePath = "/submit/";

    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> aType = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();
    private HashMap<String, String> rating = new HashMap<>();

    private String catCurrent = "";
    private String aTypeCurrent = "";
    private String speciesCurrent = "";
    private String genderCurrent = "";
    private String ratingCurrent = "";

    private String sourceFilePath;
    private String thumbnailFilePath;

    private HashMap<String, String> params = new HashMap<>();
    private open.furaffinity.client.submitPageOld.submitSubmissionPart2 submitSubmissionPart2;

    public submitSubmissionPart3(open.furaffinity.client.submitPageOld.submitSubmissionPart2 submitSubmissionPart2, String sourceFilePath, String thumbnailFilePath) {
        this.submitSubmissionPart2 = submitSubmissionPart2;
        this.sourceFilePath = sourceFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    private void processPageDataPart(String html) {
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
            }
        }

    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
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

        html = webClient[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, postParams);
        processPageDataPart(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
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

    public String getCatCurrent() {
        return catCurrent;
    }

    public String getaTypeCurrent() {
        return aTypeCurrent;
    }

    public String getSpeciesCurrent() {
        return speciesCurrent;
    }

    public String getGenderCurrent() {
        return genderCurrent;
    }

    public String getRatingCurrent() {
        return ratingCurrent;
    }


}
