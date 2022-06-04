package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.abstractClasses.abstractPage;

public class user extends abstractPage {
    private static final String pagePrefix = "/user/";

    private final String pagePath;

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

    private boolean isWatching;
    private String watchUnWatch;
    private boolean isBlocked;
    private String blockUnBlock;
    private String noteUser;

    public user(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public user(user user) {
        super(user);
        this.pagePath = user.pagePath;
    }

    public static List<HashMap<String, String>> processShouts(String html) {
        List<HashMap<String, String>> result = new ArrayList<>();

        if (html != null && html.length() > 0) {
            Document doc = Jsoup.parse(html);
            Elements commentContainersDiv = doc.select("div.comment_container");

            for (Element currentElement : commentContainersDiv) {
                HashMap<String, String> currentShoutData = new HashMap<>();

                Element shoutAvatarDiv = currentElement.selectFirst("div.shout-avatar");
                Element checkboxInput = currentElement.selectFirst("input[type=checkbox]");

                currentShoutData.put("userName", currentElement.selectFirst(".comment_username").text());
                currentShoutData.put("userIcon", "https:" + shoutAvatarDiv.selectFirst("img").attr("src"));
                currentShoutData.put("userLink", shoutAvatarDiv.selectFirst("a").attr("href"));
                currentShoutData.put("commentDate", currentElement.selectFirst(".popup_date").text());
                currentShoutData.put("comment", currentElement.selectFirst(".comment_text").html());

                if (checkboxInput != null) {
                    currentShoutData.put("checkId", checkboxInput.attr("value"));
                }

                result.add(currentShoutData);
            }
        }

        return result;
    }

    public static String getPagePrefix() {
        return pagePrefix;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUsername = doc.selectFirst("div.userpage-flex-item.username");
        userName = userPageFlexItemUsername.selectFirst("h2").text();
        userAccountStatus = "";//This was removed
        userAccountStatusLine = userPageFlexItemUsername.selectFirst("span").text();

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

        if (jsFormShout != null) {
            Element keyInput = jsFormShout.selectFirst("input[name=key]");
            Element nameInput = jsFormShout.selectFirst("input[name=name]");

            if (keyInput != null && nameInput != null) {
                shoutKey = keyInput.attr("value");
                shoutName = nameInput.attr("value");
            }
        }

        Element userNavControlsDiv = doc.selectFirst("div.user-nav-controls");

        if (userNavControlsDiv != null) {
            Elements userNavControlsDivElements = userNavControlsDiv.select("a");

            if (userNavControlsDivElements != null) {
                for (Element userNavControlsDivElement : userNavControlsDivElements) {
                    switch (userNavControlsDivElement.text()) {
                        case "+Watch":
                            isWatching = false;
                            watchUnWatch = userNavControlsDivElement.attr("href");
                            break;
                        case "-Watch":
                            isWatching = true;
                            watchUnWatch = userNavControlsDivElement.attr("href");
                            break;
                        case "Note":
                            noteUser = userNavControlsDivElement.attr("href").replace(msgPms.getNotePathPrefix(), "");
                            noteUser = noteUser.substring(0, noteUser.length() - 1);
                            break;
                        case "Block":
                            isBlocked = false;
                            blockUnBlock = userNavControlsDivElement.attr("href");
                            break;
                        case "Unblock":
                            isBlocked = true;
                            blockUnBlock = userNavControlsDivElement.attr("href");
                            break;
                    }
                }
            }
        }

        return userPageFlexItemUsername != null && userPageFlexItemUserNavAvatarDesktopImg != null && userPageFlexItemsH2 != null && userPageProfileDiv != null;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getPagePath() {
        return pagePath;
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

    public String getShoutKey() {
        return shoutKey;
    }

    public String getShoutName() {
        return shoutName;
    }

    public boolean getIsWatching() {
        return isWatching;
    }

    public String getWatchUnWatch() {
        return watchUnWatch;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public String getBlockUnBlock() {
        return blockUnBlock;
    }

    public String getNoteUser() {
        return noteUser;
    }
}
