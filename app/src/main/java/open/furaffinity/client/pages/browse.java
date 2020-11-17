package open.furaffinity.client.pages;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.imageResultsTool.getDropDownOptions;
import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

public class browse extends open.furaffinity.client.abstractClasses.page {
    private static final String TAG = browse.class.getName();

    private HashMap<String, String> requestParameters = new HashMap<>();

    private HashMap<String, String> cat = new HashMap<>();
    private HashMap<String, String> atype = new HashMap<>();
    private HashMap<String, String> species = new HashMap<>();
    private HashMap<String, String> gender = new HashMap<>();
    private HashMap<String, String> perpage = new HashMap<>();

    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public browse(Context context, pageListener pageListener) {
        super(context, pageListener);

        setPage("1");
        setRatingGeneral(true);
    }

    public browse(browse browse) {
        super(browse);

        this.cat = browse.cat;
        this.atype = browse.atype;
        this.species = browse.species;
        this.gender = browse.gender;
        this.perpage = browse.perpage;
        this.requestParameters = browse.requestParameters;
        this.pageResults = browse.pageResults;
    }

    protected void processPageData(String html) {
        cat = getDropDownOptions("cat", html);
        atype = getDropDownOptions("atype", html);
        species = getDropDownOptions("species", html);
        gender = getDropDownOptions("gender", html);
        perpage = getDropDownOptions("perpage", html);
        pageResults = getResultsData(html);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html;
        String pagePath = "/browse";
        html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, requestParameters);

        if(html != null) {
            processPageData(html);
            return true;
        }

        return false;
    }

    private void setRestrictedKeyValuePairState(String key, String value, HashMap<String, String> AllowMap) {
        if (AllowMap.containsKey(value)) {
            requestParameters.put(key, value);
        } else if (value.equals("")) {
            requestParameters.remove(key);
        }
    }

    public HashMap<String, String> getCat() {
        return cat;
    }

    public String getCurrentCat() {
        return Optional.ofNullable(requestParameters.get("cat")).orElse("");
    }

    public void setCat(String value) {
        setRestrictedKeyValuePairState("cat", value, cat);
    }

    public HashMap<String, String> getAtype() {
        return atype;
    }

    public String getCurrentAtype() {
        return Optional.ofNullable(requestParameters.get("atype")).orElse("");
    }

    public void setAtype(String value) {
        setRestrictedKeyValuePairState("atype", value, atype);
    }

    public HashMap<String, String> getSpecies() {
        return species;
    }

    public String getCurrentSpecies() {
        return Optional.ofNullable(requestParameters.get("species")).orElse("");
    }

    public void setSpecies(String value) {
        setRestrictedKeyValuePairState("species", value, species);
    }

    public HashMap<String, String> getGender() {
        return gender;
    }

    public String getCurrentGender() {
        return Optional.ofNullable(requestParameters.get("gender")).orElse("");
    }

    public void setGender(String value) {
        setRestrictedKeyValuePairState("gender", value, gender);
    }

    public HashMap<String, String> getPerpage() {
        return perpage;
    }

    public String getCurrentPerpage() {
        return Optional.ofNullable(requestParameters.get("perpage")).orElse("");
    }

    public void setPerpage(String value) {
        setRestrictedKeyValuePairState("perpage", value, perpage);
    }

    public int getPage() {
        if (requestParameters.containsKey("page")) {
            try {
                return Integer.parseInt(Objects.requireNonNull(requestParameters.get("page")));
            } catch (NumberFormatException e) {
                Log.e(TAG, "getPage: ", e);
            }
            return 1;
        } else {
            return 1;
        }
    }

    public String getCurrentPage() {
        return Optional.ofNullable(requestParameters.get("page")).orElse("1");
    }

    public void setPage(String value) {
        try {
            if (Integer.parseInt(value) > 0) {
                requestParameters.put("page", value);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "setPage: ", e);
        }
    }

    private void setCheckboxState(String key, boolean state) {
        if (state) {
            requestParameters.put(key, "on");
        } else {
            requestParameters.remove(key);
        }
    }

    public String getCurrentRatingGeneral() {
        return Optional.ofNullable(requestParameters.get("rating_general")).orElse("");
    }

    public void setRatingGeneral(boolean newSetting) {
        setCheckboxState("rating_general", newSetting);
    }

    public String getCurrentRatingMature() {
        return Optional.ofNullable(requestParameters.get("rating_mature")).orElse("");
    }

    public void setRatingMature(boolean newSetting) {
        setCheckboxState("rating_mature", newSetting);
    }

    public String getCurrentRatingAdult() {
        return Optional.ofNullable(requestParameters.get("rating_adult")).orElse("");
    }

    public void setRatingAdult(boolean newSetting) {
        setCheckboxState("rating_adult", newSetting);
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }


}