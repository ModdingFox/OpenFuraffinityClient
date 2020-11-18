package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import open.furaffinity.client.utilities.webClient;

public class controlsSiteSettings extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = controlsSiteSettings.class.getName();

    private static String pagePath = "/controls/site-settings/";

    private boolean disable_avatars;
    private String date_format;
    private HashMap<String, String> perpage;
    private String perpage_current;
    private HashMap<String, String> newsubmissions_direction;
    private String newsubmissions_direction_current;
    private HashMap<String, String> thumbnail_size;
    private String thumbnail_size_current;
    private String gallery_navigation;
    private HashMap<String, String> hide_favorites;
    private String hide_favorites_current;
    private HashMap<String, String> no_guests;
    private String no_guests_current;
    private HashMap<String, String> no_search_engines;
    private String no_search_engines_current;
    private HashMap<String, String> no_notes;
    private String no_notes_current;

    public controlsSiteSettings() {
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Elements inputs = doc.select("input[checked=checked]");
        Elements selects = doc.select("select");

        if (inputs != null) {
            for (Element input : inputs) {
                switch (input.attr("name")) {
                    case "disable_avatars":
                        disable_avatars = ((input.attr("value").equals("1")) ? (true) : (false));
                        break;
                    case "date_format":
                        date_format = input.attr("value");
                        break;
                    case "gallery_navigation":
                        gallery_navigation = input.attr("value");
                        break;
                }
            }
        }

        if (selects != null) {
            for (Element select : selects) {
                Element selected = select.selectFirst("option[selected]");
                Elements options = select.select("option");
                HashMap<String, String> newOptionList = new HashMap<>();

                for (Element option : options) {
                    if (option.hasAttr("value")) {
                        newOptionList.put(option.attr("value"), option.text());
                    }
                }

                switch (select.attr("name")) {
                    case "perpage":
                        perpage = newOptionList;
                        perpage_current = ((selected != null) ? (selected.attr("value")) : ("24"));
                        break;
                    case "newsubmissions_direction":
                        newsubmissions_direction = newOptionList;
                        newsubmissions_direction_current = ((selected != null) ? (selected.attr("value")) : ("desc"));
                        break;
                    case "thumbnail_size":
                        thumbnail_size = newOptionList;
                        thumbnail_size_current = ((selected != null) ? (selected.attr("value")) : ("250"));
                        break;
                    case "hide_favorites":
                        hide_favorites = newOptionList;
                        hide_favorites_current = ((selected != null) ? (selected.attr("value")) : ("n"));
                        break;
                    case "no_guests":
                        no_guests = newOptionList;
                        no_guests_current = ((selected != null) ? (selected.attr("value")) : ("0"));
                        break;
                    case "no_search_engines":
                        no_search_engines = newOptionList;
                        no_search_engines_current = ((selected != null) ? (selected.attr("value")) : ("0"));
                        break;
                    case "no_notes":
                        no_notes = newOptionList;
                        no_notes_current = ((selected != null) ? (selected.attr("value")) : ("0"));
                        break;
                }
            }
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public static String getPagePath() {
        return pagePath;
    }

    public boolean getDisableAvatars() {
        return disable_avatars;
    }

    public String getDateFormat() {
        return date_format;
    }

    public HashMap<String, String> getPerPage() {
        return perpage;
    }

    public String getPerPageCurrent() {
        return perpage_current;
    }

    public HashMap<String, String> getNewSubmissionsDirection() {
        return newsubmissions_direction;
    }

    public String getNewSubmissionsDirectionCurrent() {
        return newsubmissions_direction_current;
    }

    public HashMap<String, String> getThumbnailSize() {
        return thumbnail_size;
    }

    public String getThumbnailSizeCurrent() {
        return thumbnail_size_current;
    }

    public String getGalleryNavigation() {
        return gallery_navigation;
    }

    public HashMap<String, String> getHideFavorites() {
        return hide_favorites;
    }

    public String getHideFavoritesCurrent() {
        return hide_favorites_current;
    }

    public HashMap<String, String> getNoGuests() {
        return no_guests;
    }

    public String getNoGuestsCurrent() {
        return no_guests_current;
    }

    public HashMap<String, String> getNoSearchEngines() {
        return no_search_engines;
    }

    public String getNoSearchEnginesCurrent() {
        return no_search_engines_current;
    }

    public HashMap<String, String> getNoNotes() {
        return no_notes;
    }

    public String getNoNotesCurrent() {
        return no_notes_current;
    }

}
