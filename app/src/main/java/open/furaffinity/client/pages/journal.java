package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.utilities.webClient;

public class journal extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = journal.class.getName();

    private String pagePath;
    private String journalUserIcon;
    private String journalUserLink;
    private String journalUserName;
    private String journalTitle;
    private String journalDate;
    private String journalContent;
    private String journalComments;

    public journal(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element userPageFlexItemUserNavAvatarDesktop = doc.selectFirst("div.userpage-flex-item.user-nav-avatar-desktop");
        Element userPageFlexItemUserNavAvatarDesktopImg = userPageFlexItemUserNavAvatarDesktop.selectFirst("img");
        Element userPageFlexItemUserNavAvatarDesktopA = userPageFlexItemUserNavAvatarDesktop.selectFirst("a");
        journalUserIcon = "https:" + userPageFlexItemUserNavAvatarDesktopImg.attr("src");
        journalUserLink = userPageFlexItemUserNavAvatarDesktopA.attr("href");

        Element userPageFlexItemUsernameH = doc.selectFirst("div.userpage-flex-item.username").selectFirst("h2");
        journalUserName = userPageFlexItemUsernameH.text();

        Element journalTitleH2 = doc.selectFirst("h2.journal-title");
        journalTitle = journalTitleH2.text();
        journalDate = journalTitleH2.nextElementSibling().text();

        Element journalContentContainer = doc.selectFirst("div.journal-content-container").selectFirst("div.journal-content");
        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(journalContentContainer);
        journalContent = journalContentContainer.html();

        Element journalCommentsList = doc.selectFirst("div.comments-list");
        journalComments = journalCommentsList.html();
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
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

}
