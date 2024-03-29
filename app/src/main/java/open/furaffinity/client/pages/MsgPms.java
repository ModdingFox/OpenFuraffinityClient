package open.furaffinity.client.pages;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.utilities.WebClient;

public class MsgPms extends AbstractPage {
    private static final String sendPath = "/msg/send";
    private static final String notePathPrefix = "/newpm/";
    private final String pagePath = "/msg/pms";
    List<HashMap<String, String>> messages;
    private int currentPage = 1;
    private mailFolders selectedFolder = mailFolders.inbox;
    private String postKey;
    private boolean recaptchaRequired;

    public MsgPms(Context context, PageListener pageListener) {
        super(context, pageListener);
    }

    public MsgPms(MsgPms msgPms) {
        super(msgPms);
        this.currentPage = msgPms.currentPage;
        this.selectedFolder = msgPms.selectedFolder;
    }

    public static String getSendPath() {
        return sendPath;
    }

    public static String getNotePathPrefix() {
        return notePathPrefix;
    }

    protected Boolean processPageData(String html) {
        messages = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        Element MessagecenterMailListPane = doc.selectFirst("div.messagecenter-mail-list-pane");

        if (MessagecenterMailListPane != null) {
            Elements NoteListContainer =
                MessagecenterMailListPane.select("div.note-list-container");

            for (Element currentElement : NoteListContainer) {
                HashMap<String, String> newElement = new HashMap<>();

                Element messageid =
                    currentElement.selectFirst("div.note-list-checkbox-mobile-tablet>input");
                Element messageLink =
                    currentElement.selectFirst("div.note-list-subject-container>a");
                Element messageSubject = currentElement.selectFirst(
                    "div.note-list-subject-container>a>div.note-list-subject");
                Element messageSender = currentElement.selectFirst(
                    "div.note-list-sendgroup>div.note-list-sender-container>div" +
                        ".note-list-sender>div>a");
                Element messageSendDate = currentElement.selectFirst(
                    "div.note-list-sendgroup>div.note-list-senddate>span.popup_date");

                if (messageid != null) {
                    newElement.put("messageid", messageid.attr("value"));
                }
                if (messageLink != null) {
                    newElement.put("messageLink", messageLink.attr("href"));
                }
                if (messageSubject != null) {
                    newElement.put("messageSubject", messageSubject.text());
                }
                if (messageSender != null) {
                    newElement.put("messageSender", messageSender.text());
                    newElement.put("messageSenderLink", messageSender.attr("href"));
                }
                if (messageSendDate != null) {
                    newElement.put("messageSendDate", messageSendDate.text());
                }

                messages.add(newElement);
            }

            Element noteFormForm = doc.selectFirst("form[id=note-form]");
            if (noteFormForm != null) {
                Element keyInput = noteFormForm.selectFirst("input[name=key]");
                if (keyInput != null) {
                    postKey = keyInput.attr("value");
                }
            }

            Element gRecaptcha = doc.selectFirst("[id=g-recaptcha]");
            recaptchaRequired = gRecaptcha != null;

            return true;
        }

        return false;
    }

    @Override protected Boolean doInBackground(Void... Void) {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("folder", this.selectedFolder.toString());

        String html;
        html = webClient.sendGetRequest(
            WebClient.getBaseUrl() + pagePath + "/" + currentPage,
            cookies);
        if (webClient.getLastPageLoaded() && html != null) {
            return processPageData(html);
        }
        return false;
    }

    public int getPage() {
        return currentPage;
    }

    public void setPage(int value) {
        currentPage = value;
    }

    public mailFolders getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(mailFolders mailFolder) {
        selectedFolder = mailFolder;
    }

    public List<HashMap<String, String>> getMessages() {
        return messages;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getPostKey() {
        return postKey;
    }

    public boolean isRecaptchaRequired() {
        return recaptchaRequired;
    }

    public enum mailFolders {
        inbox("inbox", "Inbox"), unread("unread", "Unread"), sent("sent", "Sent"),
        high_prio("high_prio", "High priority"), medium_prio("medium_prio", "Medium priority"),
        low_prio("low_prio", "Low priority"), archive("archive", "Archive"),
        trash("trash", "Trash");

        private final String value;
        private final String printableName;

        mailFolders(String value, String printableName) {
            this.value = value;
            this.printableName = printableName;
        }

        @Override public String toString() {
            return this.value;
        }

        public String getPrintableName() {
            return this.printableName;
        }
    }

    public enum priorities {
        high("high", "High"), medium("medium", "Medium"), low("low", "Low"), none("none", "None"),
        archive("archive", "Archive"), unread("unread", "Mark Unread");

        private final String value;
        private final String printableName;

        priorities(String value, String printableName) {
            this.value = value;
            this.printableName = printableName;
        }

        @Override public String toString() {
            return this.value;
        }

        public String getPrintableName() {
            return this.printableName;
        }
    }
}
