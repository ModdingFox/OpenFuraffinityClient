package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.utilities.webClient;

public class user extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = user.class.getName();

    private boolean isLoaded = false;

    private String pagePath;

    private String userName;
    private String userIcon;
    private String userAccountStatus;
    private String userAccountStatusLine;

    private String userViews;
    private String userSubmissions;
    private String userFavs;
    private String userCommentsEarned;
    private String userCommentsMade;
    private String userJournals;

    private String userGalleryPath;
    private String userScrapsPath;
    private String userFavoritesPath;
    private String userJournalsPath;
    private String userCommissionPath;

    private String userPageProfile;
    private String userProfile;

    private String userFeaturedSubmissionPath;
    private String userFeaturedSubmissionImagePath;
    private String userFeaturedSubmissionTitle;

    private String userRecentWatchers;
    private String userWatchersPath;
    private String userRecentlyWatching;
    private String userWatchingPath;

    private String userShouts;
    private String shoutKey;
    private String shoutName;

    public user(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsernameH = doc.selectFirst("div.userpage-flex-item.username").selectFirst("h2");
        userName = userPageFlexItemUsernameH.text();
        userAccountStatus = userPageFlexItemUsernameH.selectFirst("span").attr("title");
        userAccountStatusLine = userPageFlexItemUsernameH.nextElementSibling().text();

        Element userPageFlexItemUserNavAvatarDesktopImg = doc.selectFirst("div.userpage-flex-item.user-nav-avatar-desktop").selectFirst("img");
        userIcon = "https:" + userPageFlexItemUserNavAvatarDesktopImg.attr("src");

        Elements userPageFlexItemsH2 = doc.select("div.section-header").select("h2");

        for (Element currentElement : userPageFlexItemsH2) {
            Elements currentElementsParentSectionBody = currentElement.parent().parent().select("div.section-body");

            switch (currentElement.text()) {
                case "Stats":
                    Matcher tableDataRegex = Pattern.compile("Views: ([\\d]+) Submissions: ([\\d]+) Favs: ([\\d]+) Comments Earned: ([\\d]+) Comments Made: ([\\d]+) Journals: ([\\d]+)").matcher(currentElementsParentSectionBody.get(0).text());
                    if (tableDataRegex.find()) {
                        userViews = tableDataRegex.group(1);
                        userSubmissions = tableDataRegex.group(2);
                        userFavs = tableDataRegex.group(3);
                        userCommentsEarned = tableDataRegex.group(4);
                        userCommentsMade = tableDataRegex.group(5);
                        userJournals = tableDataRegex.group(6);
                    }
                    break;
                case "User Profile":
                    userProfile = "";
                    for (Element currentSectionBodyElement : currentElementsParentSectionBody) {
                        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(currentSectionBodyElement);
                        userProfile += currentSectionBodyElement.html();
                    }
                    break;
                case "Featured Submission":
                    userFeaturedSubmissionPath = currentElementsParentSectionBody.get(0).selectFirst("a").attr("href");
                    userFeaturedSubmissionImagePath = currentElementsParentSectionBody.get(0).selectFirst("img").attr("src");
                    userFeaturedSubmissionTitle = currentElementsParentSectionBody.get(0).selectFirst("h2").text();
                    break;
                case "Recent Watchers":
                    userRecentWatchers = currentElementsParentSectionBody.html();
                    userWatchersPath = currentElement.parent().selectFirst("h3").selectFirst("a").attr("href");
                    break;
                case "Recently Watched":
                    userRecentlyWatching = currentElementsParentSectionBody.html();
                    userWatchingPath = currentElement.parent().selectFirst("h3").selectFirst("a").attr("href");
                    break;
            }
        }

        for (Element currentElement : doc.selectFirst("div.userpage-flex-item.user-nav").select("a")) {
            switch (currentElement.text()) {
                case "Gallery":
                    userGalleryPath = currentElement.attr("href");
                    break;
                case "Scraps":
                    userScrapsPath = currentElement.attr("href");
                    break;
                case "Favorites":
                    userFavoritesPath = currentElement.attr("href");
                    break;
                case "Journals":
                    userJournalsPath = currentElement.attr("href");
                    break;
                case "Commission":
                    userCommissionPath = currentElement.attr("href");
                    break;
            }
        }

        Element userPageProfileDiv = doc.selectFirst("div.userpage-profile");
        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userPageProfileDiv);
        userPageProfile = userPageProfileDiv.html();

        Element aShout = doc.selectFirst("a[id^=shout]");

        if (aShout != null && aShout.hasParent() && aShout.parent().hasParent()) {
            userShouts = aShout.parent().parent().html();
        }

        Element jsFormShout = doc.selectFirst("form[name=JSForm]");

        if(jsFormShout != null) {
            Element keyInput = jsFormShout.selectFirst("input[name=key]");
            Element nameInput = jsFormShout.selectFirst("input[name=name]");

            if (keyInput != null && nameInput != null) {
                shoutKey = keyInput.attr("value");
                shoutName = nameInput.attr("value");
            }
        }

        return;
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        isLoaded = webClient[0].getLastPageLoaded();
        if (isLoaded) {
            processPageData(html);
        }
        return null;
    }

    public String getPagePath() {
        return pagePath;
    }

    public boolean getIsLoaded() {
        return isLoaded;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAccountStatus() {
        return userAccountStatus;
    }

    public String getUserAccountStatusLine() {
        return userAccountStatusLine;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getUserViews() {
        return userViews;
    }

    public String getUserSubmissions() {
        return userSubmissions;
    }

    public String getUserFavs() {
        return userFavs;
    }

    public String getUserCommentsEarned() {
        return userCommentsEarned;
    }

    public String getUserCommentsMade() {
        return userCommentsMade;
    }

    public String getUserJournals() {
        return userJournals;
    }

    public String getUserGalleryPath() {
        return userGalleryPath;
    }

    public String getUserScrapsPath() {
        return userScrapsPath;
    }

    public String getUserFavoritesPath() {
        return userFavoritesPath;
    }

    public String getUserJournalsPath() {
        return userJournalsPath;
    }

    public String getUserCommissionPath() {
        return userCommissionPath;
    }

    public String getUserPageProfile() {
        return userPageProfile;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public String getUserFeaturedSubmissionPath() {
        return userFeaturedSubmissionPath;
    }

    public String getUserFeaturedSubmissionImagePath() {
        return userFeaturedSubmissionImagePath;
    }

    public String getUserFeaturedSubmissionTitle() {
        return userFeaturedSubmissionTitle;
    }

    public String getUserRecentWatchers() {
        return userRecentWatchers;
    }

    public String getUserWatchersPath() {
        return userWatchersPath;
    }

    public String getUserRecentlyWatching() {
        return userRecentlyWatching;
    }

    public String getUserWatchingPath() {
        return userWatchingPath;
    }

    public String getUserShouts() {
        return userShouts;
    }

    public static List<HashMap<String, String>> processShouts(String html) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements commentContainersDiv = doc.select("div.comment_container");

            for (Element currentElement : commentContainersDiv) {
                HashMap<String, String> currentShoutData = new HashMap<>();

                Element shoutAvatarDiv = currentElement.selectFirst("div.shout-avatar");

                currentShoutData.put("userName", currentElement.selectFirst("div.comment_username").text());
                currentShoutData.put("userIcon", "https:" + shoutAvatarDiv.selectFirst("img").attr("src"));
                currentShoutData.put("userLink", shoutAvatarDiv.selectFirst("a").attr("href"));
                currentShoutData.put("commentDate", currentElement.selectFirst("div.shout-date").text());
                currentShoutData.put("comment", currentElement.selectFirst("div.body.comment_text").html());

                result.add(currentShoutData);
            }
        }

        return result;
    }

    public String getShoutKey() {
        return shoutKey;
    }

    public String getShoutName() {
        return shoutName;
    }
}
