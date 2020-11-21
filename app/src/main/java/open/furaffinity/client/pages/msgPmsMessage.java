package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import open.furaffinity.client.abstractClasses.abstractPage;

public class msgPmsMessage extends abstractPage {
    private String pagePath;
    private String messageSubject;
    private String messageUserIcon;
    private String messageUserLink;
    private String messageSentBy;
    private String messageSentTo;
    private String messageSentDate;
    private String messageBody;

    public msgPmsMessage(Context context, pageListener pageListener, String pagePath) {
        super(context, pageListener);
        this.pagePath = pagePath;
    }

    public msgPmsMessage(msgPmsMessage msgPmsMessage) {
        super(msgPmsMessage);
        this.pagePath = msgPmsMessage.pagePath;
    }

    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        Element messageSection = doc.selectFirst("section[id=message]");

        if(messageSection != null) {
            Element messageSectionSectionHeaderDiv = messageSection.selectFirst("div.section-header");
            if(messageSectionSectionHeaderDiv != null) {
                Element messageSectionSectionHeaderDivH2 = messageSectionSectionHeaderDiv.selectFirst("h2");
                if(messageSectionSectionHeaderDivH2 != null) {
                    messageSubject = messageSectionSectionHeaderDivH2.text();
                }

                Element messageSectionSectionHeaderDivAvatarDiv = messageSectionSectionHeaderDiv.selectFirst("div.avatar");
                if(messageSectionSectionHeaderDivAvatarDiv != null) {
                    Element messageSectionSectionHeaderDivAvatarDivA = messageSectionSectionHeaderDivAvatarDiv.selectFirst("a");
                    if(messageSectionSectionHeaderDivAvatarDivA != null) {
                        Element messageSectionSectionHeaderDivAvatarDivAvatarImg = messageSectionSectionHeaderDivAvatarDiv.selectFirst("img.avatar");
                        if(messageSectionSectionHeaderDivAvatarDivAvatarImg != null) {
                            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(messageSectionSectionHeaderDivAvatarDivAvatarImg);
                            messageUserIcon = messageSectionSectionHeaderDivAvatarDivAvatarImg.attr("src");
                        }
                        messageUserLink = messageSectionSectionHeaderDivAvatarDivA.attr("href");
                    }
                }

                Element messageSectionSectionHeaderDivAddressesDiv = messageSectionSectionHeaderDiv.selectFirst("div.addresses");
                if(messageSectionSectionHeaderDivAddressesDiv != null) {
                    Elements messageSectionSectionHeaderDivAddressesDivA = messageSectionSectionHeaderDivAddressesDiv.select("a");
                    if(messageSectionSectionHeaderDivAddressesDivA != null && messageSectionSectionHeaderDivAddressesDivA.size() == 2) {
                        messageSentBy = messageSectionSectionHeaderDivAddressesDivA.get(0).text();
                        messageSentTo = messageSectionSectionHeaderDivAddressesDivA.get(1).text();
                    }

                    Element messageSectionSectionHeaderDivAddressesDivPopupDateSpan = messageSectionSectionHeaderDivAddressesDiv.selectFirst("span.popup_date");
                    if(messageSectionSectionHeaderDivAddressesDivPopupDateSpan != null) {
                        messageSentDate = messageSectionSectionHeaderDivAddressesDivPopupDateSpan.text();
                    }
                }
            }

            Element messageSectionSectionBodyDiv = messageSection.selectFirst("div.section-body");
            if(messageSectionSectionBodyDiv != null) {
                Element messageSectionSectionBodyDivUserSubmittedLinksDiv = messageSectionSectionBodyDiv.selectFirst("div.user-submitted-links");
                if(messageSectionSectionBodyDivUserSubmittedLinksDiv != null) {
                    open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(messageSectionSectionBodyDivUserSubmittedLinksDiv);
                    messageBody = messageSectionSectionBodyDivUserSubmittedLinksDiv.outerHtml();
                }
            }

            if(messageSectionSectionHeaderDiv != null && messageSectionSectionBodyDiv != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        String html;
        html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public String getPagePath() {
        return pagePath;
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
        String user = messageUserLink.replace(open.furaffinity.client.pages.user.getPagePrefix(), "");
        return user.substring(0, user.length() - 1);
    }
}
