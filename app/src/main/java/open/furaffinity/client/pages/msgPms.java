package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.utilities.webClient;

public class msgPms extends AsyncTask<webClient, Void, Void> {
    public static enum mailFolders {
        inbox("inbox", "Inbox"), unread("unread", "Unread"), outbox("outbox", "Outbox"), high_prio("high_prio", "High priority"), medium_prio("medium_prio", "Medium priority"), low_prio("low_prio", "Low priority"), archive("archive", "Archive"), trash("trash", "Trash");

        private String value;
        private String printableName;

        mailFolders(String value) {
            this.value = value;
            this.printableName = value;
        }

        mailFolders(String value, String printableName) {
            this.value = value;
            this.printableName = printableName;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String getPrintableName() {
            return this.printableName;
        }
    }

    public static enum priorities {
        high("high", "High"), medium("medium", "Medium"), low("low", "Low"), none("none", "None"), archive("archive", "Archive"), unread("unread", "Mark Unread");

        private String value;
        private String printableName;

        priorities(String value) {
            this.value = value;
            this.printableName = value;
        }

        priorities(String value, String printableName) {
            this.value = value;
            this.printableName = printableName;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String getPrintableName() {
            return this.printableName;
        }
    }

    private String pagePath = "/msg/pms";
    private static String sendPath = "/msg/send";
    private static String notePathPrefix = "/newpm/";
    private int currentPage = 1;

    private mailFolders selectedFolder = mailFolders.inbox;
    private String postKey;
    private boolean recaptchaRequired;

    List<HashMap<String, String>> messages;

    public msgPms() {
    }

    public msgPms(msgPms msgPms) {
        this.currentPage = msgPms.currentPage;
        this.selectedFolder = msgPms.selectedFolder;
    }

    private void processPageData(String html) {
        messages = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        Element MessagecenterMailListPane = doc.selectFirst("div.messagecenter-mail-list-pane");
        Elements NoteListContainer = MessagecenterMailListPane.select("div.note-list-container");

        for (Element currentElement : NoteListContainer) {
            HashMap<String, String> newElement = new HashMap<>();

            Element messageid = currentElement.selectFirst("div.note-list-checkbox-mobile-tablet>input");
            Element messageLink = currentElement.selectFirst("div.note-list-subject-container>a");
            Element messageSubject = currentElement.selectFirst("div.note-list-subject-container>a>div.note-list-subject");
            Element messageSender = currentElement.selectFirst("div.note-list-sendgroup>div.note-list-sender-container>div.note-list-sender>div>a");
            Element messageSendDate = currentElement.selectFirst("div.note-list-sendgroup>div.note-list-senddate>span.popup_date");

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
        if(noteFormForm != null) {
            Element keyInput = noteFormForm.selectFirst("input[name=key]");
            if(keyInput != null) {
                postKey = keyInput.attr("value");
            }
        }

        Element gRecaptcha = doc.selectFirst("[id=g-recaptcha]");
        if(gRecaptcha == null) {
            recaptchaRequired = false;
        } else {
            recaptchaRequired = true;
        }
    }

    @Override
    protected Void doInBackground(webClient... webClient) {
        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("folder", this.selectedFolder.toString());

        String html;
        html = webClient[0].sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath + "/" + currentPage, cookies);
        processPageData(html);
        return null;
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

    public static String getSendPath() {
        return sendPath;
    }

    public static String getNotePathPrefix() { return notePathPrefix; }

    public boolean isRecaptchaRequired() {
        return recaptchaRequired;
    }
}
