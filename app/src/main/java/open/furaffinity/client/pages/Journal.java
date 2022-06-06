package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.Html;
import open.furaffinity.client.utilities.WebClient;

public class Journal extends AbstractPage {
    private final String pagePath;
    private String journalUserIcon;
    private String journalUserLink;
    private String journalUserName;
    private String journalTitle;
    private String journalDate;
    private String journalContent;
    private String journalComments;

    private boolean isWatching;
    private String watchUnWatch;
    private String noteUser;

    public Journal(Context context, PageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public Journal(Journal journal) {
        super(journal);
        this.pagePath = journal.pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUserNavAvatarDesktop =
            doc.selectFirst("div.userpage-flex-item.user-nav-avatar-desktop");
        Element userPageFlexItemUserNavAvatarDesktopImg =
            userPageFlexItemUserNavAvatarDesktop.selectFirst("img");
        Element userPageFlexItemUserNavAvatarDesktopA =
            userPageFlexItemUserNavAvatarDesktop.selectFirst("a");
        journalUserIcon = "https:" + userPageFlexItemUserNavAvatarDesktopImg.attr("src");
        journalUserLink = userPageFlexItemUserNavAvatarDesktopA.attr("href");

        Element userPageFlexItemUsernameH =
            doc.selectFirst("div.userpage-flex-item.username").selectFirst("h2");
        journalUserName = userPageFlexItemUsernameH.text();

        Element journalTitleH2 = doc.selectFirst("h2.journal-title");
        journalTitle = journalTitleH2.text();
        journalDate = journalTitleH2.nextElementSibling().text();

        Element journalContentContainer =
            doc.selectFirst("div.journal-content-container").selectFirst("div.journal-content");
        Html.correctHtmlAHrefAndImgScr(journalContentContainer);
        journalContent = journalContentContainer.html();

        Element journalCommentsList = doc.selectFirst("div.comments-list");
        journalComments = journalCommentsList.html();

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
                            noteUser = userNavControlsDivElement.attr("href")
                                .replace(MsgPms.getNotePathPrefix(), "");
                            noteUser = noteUser.substring(0, noteUser.length() - 1);
                            break;
                    }
                }
            }
        }

        return journalTitleH2 != null && journalContentContainer != null;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        String html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getJournalUserIcon() {
        return journalUserIcon;
    }

    public String getJournalUserLink() {
        return journalUserLink;
    }

    public String getJournalUserName() {
        return journalUserName;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public String getJournalDate() {
        return journalDate;
    }

    public String getJournalContent() {
        return journalContent;
    }

    public String getJournalComments() {
        return journalComments;
    }

    public boolean getIsWatching() {
        return isWatching;
    }

    public String getWatchUnWatch() {
        return watchUnWatch;
    }

    public String getNoteUser() {
        return noteUser;
    }

}
