package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import open.furaffinity.client.utilities.webClient;

public class msgPmsMessage extends AsyncTask<webClient, Void, Void> {
    private static final String TAG = msgPmsMessage.class.getName();

    private String pagePath;
    private String messageSubject;
    private String messageUserIcon;
    private String messageUserLink;
    private String messageSentBy;
    private String messageSentTo;
    private String messageSentDate;
    private String messageBody;

    public msgPmsMessage(String pagePath) {
        this.pagePath = pagePath;
    }

    private void processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element messageSection = doc.selectFirst("section[id=message]");

        Element messageSectionSectionHeaderDiv = messageSection.selectFirst("div.section-header");
        Element messageSectionSectionHeaderDivH2 = messageSectionSectionHeaderDiv.selectFirst("h2");
        Element messageSectionSectionHeaderDivAvatarDiv = messageSectionSectionHeaderDiv.selectFirst("div.avatar");
        Element messageSectionSectionHeaderDivAvatarDivA = messageSectionSectionHeaderDivAvatarDiv.selectFirst("a");
        Element messageSectionSectionHeaderDivAvatarDivAvatarImg = messageSectionSectionHeaderDivAvatarDiv.selectFirst("img.avatar");

        Element messageSectionSectionHeaderDivAddressesDiv = messageSectionSectionHeaderDiv.selectFirst("div.addresses");
        Elements messageSectionSectionHeaderDivAddressesDivA = messageSectionSectionHeaderDivAddressesDiv.select("a");
        Element messageSectionSectionHeaderDivAddressesDivPopupDateSpan = messageSectionSectionHeaderDivAddressesDiv.selectFirst("span.popup_date");

        Element messageSectionSectionBodyDiv = messageSection.selectFirst("div.section-body");
        Element messageSectionSectionBodyDivUserSubmittedLinksDiv = messageSectionSectionBodyDiv.selectFirst("div.user-submitted-links");

        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(messageSectionSectionHeaderDivAvatarDivAvatarImg);
        open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(messageSectionSectionBodyDivUserSubmittedLinksDiv);

        messageSubject = messageSectionSectionHeaderDivH2.text();
        messageUserIcon = messageSectionSectionHeaderDivAvatarDivAvatarImg.attr("src");
        messageUserLink = messageSectionSectionHeaderDivAvatarDivA.attr("href");
        messageSentBy = messageSectionSectionHeaderDivAddressesDivA.get(0).text();
        messageSentTo = messageSectionSectionHeaderDivAddressesDivA.get(1).text();
        messageSentDate = messageSectionSectionHeaderDivAddressesDivPopupDateSpan.text();
        messageBody = messageSectionSectionBodyDivUserSubmittedLinksDiv.outerHtml();
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        processPageData(html);
        return null;
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public String getMessageUserIcon() {
        return messageUserIcon;
    }

    public String getMessageUserLink() {
        return messageUserLink;
    }

    public String getMessageSentBy() {
        return messageSentBy;
    }

    public String getMessageSentTo() {
        return messageSentTo;
    }

    public String getMessageSentDate() {
        return messageSentDate;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageUser() {
        String user = messageUserLink.replace(open.furaffinity.client.pagesOld.user.getPagePrefix(), "");
        return user.substring(0, user.length() - 1);
    }
}
