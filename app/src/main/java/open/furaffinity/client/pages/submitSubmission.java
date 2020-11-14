package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class submitSubmission extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = submitSubmission.class.getName();

    private static String pagePath = "/submit/";

    private HashMap<String, String> submissionType = new HashMap<>();
    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> aType = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();
    private HashMap<String, String> rating = new HashMap<>();

    private String submissionTypeCurrent = "";
    private String catCurrent = "";
    private String aTypeCurrent = "";
    private String speciesCurrent = "";
    private String genderCurrent = "";
    private String ratingCurrent = "";

    private HashMap<String, String> params = new HashMap<>();

    public submitSubmission() {
    }

    public submitSubmission(submitSubmission submitSubmission) {
        this.pagePath = submitSubmission.pagePath;
        this.submissionType = submitSubmission.submissionType;
        this.cat = submitSubmission.cat;
        this.aType = submitSubmission.aType;
        this.species = submitSubmission.species;
        this.gender = submitSubmission.gender;
        this.rating = submitSubmission.rating;
        this.params = submitSubmission.params;
    }

    private void processPageDataPart(String html) {
        Document doc = Jsoup.parse(html);

        Elements submissionTypeInputs = doc.select("input[name=submission_type]");
        Element catSelect = doc.selectFirst("select[name=cat]");
        Element atypeSelect = doc.selectFirst("select[name=atype]");
        Element speciesSelect = doc.selectFirst("select[name=species]");
        Element genderSelect = doc.selectFirst("select[name=gender]");
        Elements ratingInputs = doc.select("input[name=rating]");
        Element myformForm = doc.selectFirst("form[id=myform]");

        if(submissionTypeInputs != null) {
            for(Element submissionTypeInput : submissionTypeInputs) {
                String key = submissionTypeInput.attr("value");
                String value = submissionTypeInput.parent().text().trim();

                if(submissionTypeInput.hasAttr("checked")) {
                    submissionTypeCurrent = key;
                }

                submissionType.put(key, value);
            }
        }

        cat = open.furaffinity.client.utilities.html.getDropDownOptions(catSelect);
        aType = open.furaffinity.client.utilities.html.getDropDownOptions(atypeSelect);
        species = open.furaffinity.client.utilities.html.getDropDownOptions(speciesSelect);
        gender = open.furaffinity.client.utilities.html.getDropDownOptions(genderSelect);

        if(ratingInputs != null) {
            for(Element ratingInput : ratingInputs) {
                Element nextDiv = ratingInput.nextElementSibling();
                if(nextDiv != null) {
                    rating.put(ratingInput.attr("value"), nextDiv.text());
                }
            }
        }

        if(myformForm != null) {
            params = new HashMap<>();

            Elements hiddenInputs = myformForm.select("inputs");

            if(hiddenInputs != null) {
                for(Element hiddenInput : hiddenInputs) {
                    params.put(hiddenInput.attr("name"), hiddenInput.attr("value"));
                }
            }
        }

    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageDataPart(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public HashMap<String, String> getSubmissionType() {
        return submissionType;
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

    public String getSubmissionTypeCurrent() {
        return submissionTypeCurrent;
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

    public HashMap<String, String> getParams() { return params; }
}
