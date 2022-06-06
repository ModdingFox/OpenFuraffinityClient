package open.furaffinity.client.submitPages;

import android.content.Context;
import java.util.HashMap;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.pages.ControlsSiteSettings;
import open.furaffinity.client.utilities.WebClient;

public class SubmitControlsSiteSettings extends AbstractPage {
    private final boolean disable_avatars_yes;
    private final boolean disable_avatars_no;
    private final boolean switch_date_format_full;
    private final boolean switch_date_format_fuzzy;
    private final String perpage;
    private final String newsubmissions_direction;
    private final String thumbnail_size;
    private final boolean gallery_navigation_minigallery;
    private final boolean gallery_navigation_links;
    private final String hide_favorites;
    private final String no_guests;
    private final String no_search_engines;
    private final String no_notes;

    public SubmitControlsSiteSettings(Context context, PageListener pageListener,
                                      boolean disable_avatars_yes, boolean disable_avatars_no,
                                      boolean switch_date_format_full,
                                      boolean switch_date_format_fuzzy, String perpage,
                                      String newsubmissions_direction, String thumbnail_size,
                                      boolean gallery_navigation_minigallery,
                                      boolean gallery_navigation_links, String hide_favorites,
                                      String no_guests, String no_search_engines, String no_notes) {
        super(context, pageListener);
        this.disable_avatars_yes = disable_avatars_yes;
        this.disable_avatars_no = disable_avatars_no;
        this.switch_date_format_full = switch_date_format_full;
        this.switch_date_format_fuzzy = switch_date_format_fuzzy;
        this.perpage = perpage;
        this.newsubmissions_direction = newsubmissions_direction;
        this.thumbnail_size = thumbnail_size;
        this.gallery_navigation_minigallery = gallery_navigation_minigallery;
        this.gallery_navigation_links = gallery_navigation_links;
        this.hide_favorites = hide_favorites;
        this.no_guests = no_guests;
        this.no_search_engines = no_search_engines;
        this.no_notes = no_notes;
    }

    @Override protected Boolean processPageData(String html) {
        return true;
    }

    @Override protected Boolean doInBackground(Void... voids) {
        HashMap<String, String> params = new HashMap<>();
        params.put("do", "update");

        if (disable_avatars_yes) {
            params.put("disable_avatars", "1");
        }

        if (disable_avatars_no) {
            params.put("disable_avatars", "0");
        }

        if (switch_date_format_full) {
            params.put("date_format", "full");
        }

        if (switch_date_format_fuzzy) {
            params.put("date_format", "fuzzy");
        }

        params.put("perpage", perpage);
        params.put("newsubmissions_direction", newsubmissions_direction);
        params.put("thumbnail_size", thumbnail_size);

        if (gallery_navigation_minigallery) {
            params.put("gallery_navigation", "minigallery");
        }

        if (gallery_navigation_links) {
            params.put("gallery_navigation", "links");
        }

        params.put("hide_favorites", hide_favorites);
        params.put("no_guests", no_guests);
        params.put("no_search_engines", no_search_engines);
        params.put("no_notes", no_notes);
        params.put("save_settings", "Save Settings");

        String html = webClient.sendPostRequest(
            WebClient.getBaseUrl() +
                ControlsSiteSettings.getPagePath(), params);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }
}
