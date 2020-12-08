package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.fragmentDrawers.journal;
import open.furaffinity.client.fragmentDrawers.view;

public class msgOthers extends abstractPage {
    private final String pagePath = "/msg/others";

    private String watches = "";
    private String submissionComments = "";
    private String journalComments = "";
    private String shouts = "";
    private String favorites = "";
    private String journals = "";

    public msgOthers(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public msgOthers(msgOthers msgOthers) {
        super(msgOthers);
    }

    public static List<HashMap<String, String>> processWatchNotifications(String html, String actionText) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements listElements = doc.select("li");

            for (Element currentElement : listElements) {
                HashMap<String, String> currentElementResult = new HashMap<>();

                Element currentElementAvatarDiv = currentElement.selectFirst("div.avatar");
                Element currentElementAvatarDivA = currentElementAvatarDiv.selectFirst("a");
                Element currentElementAvatarDivAImg = currentElementAvatarDivA.selectFirst("img.avatar");

                Element currentElementInputCheckbox = currentElement.selectFirst("input[type=checkbox]");

                Element currentElementInfoDiv = currentElement.selectFirst("div.info");
                Element currentElementInfoDivSpan = currentElementInfoDiv.selectFirst("span");
                Element currentElementInfoDivSpanPopupDate = currentElementInfoDiv.selectFirst("span.popup_date");

                open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(currentElementAvatarDivAImg);

                currentElementResult.put("userLink", currentElementAvatarDivA.attr("href"));
                currentElementResult.put("userIcon", currentElementAvatarDivAImg.attr("src"));
                currentElementResult.put("notificationId", currentElementInputCheckbox.attr("value"));
                currentElementResult.put("userName", currentElementInfoDivSpan.text());
                currentElementResult.put("time", currentElementInfoDivSpanPopupDate.text());

                result.add(currentElementResult);
            }
        }

        return result;
    }

    public static List<HashMap<String, String>> processShoutNotifications(String html, String actionText) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements listElements = doc.select("li");

            for (Element currentElement : listElements) {
                HashMap<String, String> currentElementResult = new HashMap<>();

                Element currentElementInputCheckbox = currentElement.selectFirst("input[type=checkbox]");
                Element currentElementAHref = currentElement.selectFirst("a");
                Element currentElementStrong = currentElement.selectFirst("strong");
                Element currentElementSpan = currentElement.selectFirst("span.popup_date");

                currentElementResult.put("notificationId", currentElementInputCheckbox.attr("value"));

                if (currentElementAHref != null && currentElementStrong != null && currentElementSpan != null) {
                    currentElementResult.put("userLink", currentElementAHref.attr("href"));
                    currentElementResult.put("userName", currentElementStrong.text());
                    currentElementResult.put("time", currentElementSpan.text());
                    currentElementResult.put("actionText", actionText);
                } else {
                    currentElementResult.put("actionText", "Shout has been removed from your page.");
                }

                result.add(currentElementResult);
            }
        }

        return result;
    }

    public static List<HashMap<String, String>> processLineNotifications(String html, String actionText) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements listElements = doc.select("li");

            for (Element currentElement : listElements) {
                HashMap<String, String> currentElementResult = new HashMap<>();

                Element currentElementInputCheckbox = currentElement.selectFirst("input[type=checkbox]");
                Elements currentElementAHref = currentElement.select("a");
                Elements currentElementStrong = currentElement.select("strong");
                Element currentElementSpan = currentElement.selectFirst("span.popup_date");

                currentElementResult.put("notificationId", currentElementInputCheckbox.attr("value"));
                currentElementResult.put("userLink", currentElementAHref.get(0).attr("href"));
                currentElementResult.put("userName", currentElementStrong.get(0).text());
                currentElementResult.put("postLink", currentElementAHref.get(1).attr("href"));
                currentElementResult.put("postName", currentElementStrong.get(1).text());
                currentElementResult.put("postClass", view.class.getName());
                currentElementResult.put("time", currentElementSpan.text());
                currentElementResult.put("actionText", actionText);

                result.add(currentElementResult);
            }
        }

        return result;
    }

    public static List<HashMap<String, String>> processJournalLineNotifications(String html, String actionText) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements listElements = doc.select("li");

            for (Element currentElement : listElements) {
                HashMap<String, String> currentElementResult = new HashMap<>();

                Element currentElementInputCheckbox = currentElement.selectFirst("input[type=checkbox]");
                Elements currentElementAHref = currentElement.select("a");
                Elements currentElementStrong = currentElement.select("strong");
                Elements currentElementB = currentElement.select("b");
                Element currentElementSpan = currentElement.selectFirst("span.popup_date");

                currentElementResult.put("notificationId", currentElementInputCheckbox.attr("value"));
                currentElementResult.put("userLink", currentElementAHref.get(0).attr("href"));
                currentElementResult.put("userName", currentElementStrong.get(0).text());
                currentElementResult.put("postLink", currentElementAHref.get(1).attr("href"));
                currentElementResult.put("postName", currentElementB.get(0).text());
                currentElementResult.put("postClass", journal.class.getName());
                currentElementResult.put("time", currentElementSpan.text());
                currentElementResult.put("actionText", actionText);

                result.add(currentElementResult);
            }
        }

        return result;
    }

    public static List<HashMap<String, String>> processJournalNotifications(String html, String actionText) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements listElements = doc.select("li");

            for (Element currentElement : listElements) {
                HashMap<String, String> currentElementResult = new HashMap<>();

                Element currentElementInputCheckbox = currentElement.selectFirst("input[type=checkbox]");
                Elements currentElementAHref = currentElement.select("a");
                Elements currentElementStrong = currentElement.select("strong");
                Element currentElementSpan = currentElement.selectFirst("span.popup_date");

                currentElementResult.put("notificationId", currentElementInputCheckbox.attr("value"));
                currentElementResult.put("postLink", currentElementAHref.get(0).attr("href"));
                currentElementResult.put("postName", currentElementStrong.get(0).text());
                currentElementResult.put("postClass", journal.class.getName());
                currentElementResult.put("userLink", currentElementAHref.get(1).attr("href"));
                currentElementResult.put("userName", currentElementStrong.get(1).text());
                currentElementResult.put("time", currentElementSpan.text());
                currentElementResult.put("actionText", actionText);

                result.add(currentElementResult);
            }
        }

        return result;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element msgOthersMessagesWatchesMessageStream = doc.selectFirst("section[id=messages-watches]");
        if (msgOthersMessagesWatchesMessageStream != null) {
            watches = msgOthersMessagesWatchesMessageStream.selectFirst("ul.message-stream").html();
        }

        Element msgOthersMessagesCommentsSubmissionMessageStream = doc.selectFirst("section[id=messages-comments-submission]");
        if (msgOthersMessagesCommentsSubmissionMessageStream != null) {
            submissionComments = msgOthersMessagesCommentsSubmissionMessageStream.selectFirst("ul.message-stream").html();
        }

        Element msgOthersMessagesCommentsJournalMessageStream = doc.selectFirst("section[id=messages-comments-journal]");
        if (msgOthersMessagesCommentsJournalMessageStream != null) {
            journalComments = msgOthersMessagesCommentsJournalMessageStream.selectFirst("ul.message-stream").html();
        }

        Element msgOthersMessagesShoutsMessageStream = doc.selectFirst("section[id=messages-shouts]");
        if (msgOthersMessagesShoutsMessageStream != null) {
            shouts = msgOthersMessagesShoutsMessageStream.selectFirst("ul.message-stream").html();
        }

        Element msgOthersMessagesFavoritesMessageStream = doc.selectFirst("section[id=messages-favorites]");
        if (msgOthersMessagesFavoritesMessageStream != null) {
            favorites = msgOthersMessagesFavoritesMessageStream.selectFirst("ul.message-stream").html();
        }

        Element msgOthersMessagesJournalsMessageStream = doc.selectFirst("section[id=messages-journals]");
        if (msgOthersMessagesJournalsMessageStream != null) {
            journals = msgOthersMessagesJournalsMessageStream.selectFirst("ul.message-stream").html();
        }

        //this is not great. would really like a way to actually check that the page loaded
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getWatches() {
        return watches;
    }

    public String getSubmissionComments() {
        return submissionComments;
    }

    public String getJournalComments() {
        return journalComments;
    }

    public String getShouts() {
        return shouts;
    }

    public String getFavorites() {
        return favorites;
    }

    public String getJournals() {
        return journals;
    }

    public String getPagePath() {
        return pagePath;
    }
}
