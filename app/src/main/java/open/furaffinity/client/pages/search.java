package open.furaffinity.client.pages;

import static open.furaffinity.client.utilities.imageResultsTool.getDropDownOptions;
import static open.furaffinity.client.utilities.imageResultsTool.getResultsData;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.fragmentDrawers.settings;
import open.furaffinity.client.utilities.imageResultsTool;

public class search extends BasePage {
    private static final String pagePath = "/search";

    private static final List<String> rangeAllowedKeys = Arrays.asList("day", "3days", "week", "month", "all");
    private static final List<String> modeAllowedKeys = Arrays.asList("all", "any", "extended");
    private final imageResultsTool.imageResolutions currentResolution;
    private HashMap<String, String> requestParameters = new HashMap<>();
    private HashMap<String, String> orderBy = new HashMap<>();
    private HashMap<String, String> orderDirection = new HashMap<>();
    private List<HashMap<String, String>> pageResults = new ArrayList<>();

    public search(Context context, pageListener pageListener) {
        super(context, pageListener);
        setPage("1");
        setRatingGeneral(true);
        requestParameters.put("order-by", "relevancy");//hacky but works for now
        requestParameters.put("order-direction", "desc");//hacky but works for now
        setTypeArt(true);

        currentResolution = imageResultsTool.getimageResolutionFromInt(sharedPref.getInt(context.getString(R.string.imageResolutionSetting), settings.imageResolutionDefault));
    }

    public search(search search) {
        super(search);
        this.orderBy = search.orderBy;
        this.orderDirection = search.orderDirection;
        this.requestParameters = search.requestParameters;
        this.pageResults = search.pageResults;
        this.currentResolution = search.currentResolution;
    }

    protected Boolean processPageData(String html) {
        orderBy = getDropDownOptions("order-by", html);
        orderDirection = getDropDownOptions("order-direction", html);
        pageResults = getResultsData(html, currentResolution);

        return orderBy != null && orderDirection != null;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, requestParameters);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getCurrentQuery() {
        return Optional.ofNullable(requestParameters.get("q")).orElse("");
    }

    public void setQuery(String value) {
        requestParameters.put("q", value);
    }

    public HashMap<String, String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String value) {
        if (orderBy.containsKey(value)) {
            requestParameters.put("order-by", value);
        }
    }

    public String getCurrentOrderBy() {
        return Optional.ofNullable(requestParameters.get("order-by")).orElse("");
    }

    public HashMap<String, String> getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String value) {
        if (orderDirection.containsKey(value)) {
            requestParameters.put("order-direction", value);
        }
    }

    public String getCurrentOrderDirection() {
        return Optional.ofNullable(requestParameters.get("order-direction")).orElse("");
    }

    public String getCurrentRange() {
        return Optional.ofNullable(requestParameters.get("range")).orElse("");
    }

    public void setRange(String value) {
        if (rangeAllowedKeys.contains(value)) {
            requestParameters.put("range", value);
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
        return Optional.ofNullable(requestParameters.get("rating-general")).orElse("");
    }

    public void setRatingGeneral(boolean newSetting) {
        setCheckboxState("rating-general", newSetting);
    }

    public String getCurrentRatingMature() {
        return Optional.ofNullable(requestParameters.get("rating-mature")).orElse("");
    }

    public void setRatingMature(boolean newSetting) {
        setCheckboxState("rating-mature", newSetting);
    }

    public String getCurrentRatingAdult() {
        return Optional.ofNullable(requestParameters.get("rating-adult")).orElse("");
    }

    public void setRatingAdult(boolean newSetting) {
        setCheckboxState("rating-adult", newSetting);
    }

    public String getCurrentTypeArt() {
        return Optional.ofNullable(requestParameters.get("type-art")).orElse("");
    }

    public void setTypeArt(boolean newSetting) {
        setCheckboxState("type-art", newSetting);
    }

    public String getCurrentTypeMusic() {
        return Optional.ofNullable(requestParameters.get("type-music")).orElse("");
    }

    public void setTypeMusic(boolean newSetting) {
        setCheckboxState("type-music", newSetting);
    }

    public String getCurrentTypeFlash() {
        return Optional.ofNullable(requestParameters.get("type-flash")).orElse("");
    }

    public void setTypeFlash(boolean newSetting) {
        setCheckboxState("type-flash", newSetting);
    }

    public String getCurrentTypeStory() {
        return Optional.ofNullable(requestParameters.get("type-story")).orElse("");
    }

    public void setTypeStory(boolean newSetting) {
        setCheckboxState("type-story", newSetting);
    }

    public String getCurrentTypePhoto() {
        return Optional.ofNullable(requestParameters.get("type-photo")).orElse("");
    }

    public void setTypePhoto(boolean newSetting) {
        setCheckboxState("type-photo", newSetting);
    }

    public String getCurrentTypePoetry() {
        return Optional.ofNullable(requestParameters.get("type-poetry")).orElse("");
    }

    public void setTypePoetry(boolean newSetting) {
        setCheckboxState("type-poetry", newSetting);
    }

    public String getCurrentMode() {
        return Optional.ofNullable(requestParameters.get("mode")).orElse("");
    }

    public void setMode(String value) {
        if (modeAllowedKeys.contains(value)) {
            requestParameters.put("mode", value);
        }
    }

    public int getPage() {
        if (requestParameters.containsKey("page")) {
            try {
                return Integer.parseInt(Objects.requireNonNull(requestParameters.get("page")));
            } catch (NumberFormatException e) {
                Log.e(TAG, "getPage: ", e);
            }
        }
        return 1;
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

    public String getCurrentPage() {
        return Optional.ofNullable(requestParameters.get("page")).orElse("1");
    }

    public List<HashMap<String, String>> getPageResults() {
        return pageResults;
    }
}
