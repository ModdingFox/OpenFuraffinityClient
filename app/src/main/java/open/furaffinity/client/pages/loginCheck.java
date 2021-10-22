package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import open.furaffinity.client.abstractClasses.abstractPage;

public class loginCheck extends abstractPage {
    private boolean isLoggedIn = false;
    private boolean isNSFWAllowed = false;
    private String userIcon = "";
    private String userName = "";
    private String userPage = "";

    private int notificationS;
    private int notificationW;
    private int notificationC;
    private int notificationF;
    private int notificationJ;
    private int notificationN;

    public loginCheck(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public loginCheck(loginCheck loginCheck) {
        super(loginCheck);
        this.isLoggedIn = loginCheck.isLoggedIn;
        this.isNSFWAllowed = loginCheck.isNSFWAllowed;
        this.userIcon = loginCheck.userIcon;
        this.userName = loginCheck.userName;
        this.userPage = loginCheck.userPage;
    }

    @Override
    protected Boolean processPageData(String html) {
        Document doc = Jsoup.parse(html);

        if (doc != null) {
            if (doc.selectFirst("a[href=/login]") == null) {
                isLoggedIn = true;

                Element nsfwToggle = doc.selectFirst("input.slider-toggle[id=sfw-toggle-mobile]");

                if (nsfwToggle != null) {
                    this.isNSFWAllowed = true;
                }

                Element userIconImg = doc.selectFirst("img.loggedin_user_avatar");

                if (userIconImg != null) {
                    open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userIconImg);

                    userIcon = userIconImg.attr("src");
                    userName = userIconImg.attr("alt");
                    userPage = userIconImg.parent().attr("href");
                }

                Elements notifications = doc.select("a.notification-container");

                for (Element notification : notifications) {
                    String notificationText = notification.text();

                    switch (notificationText.substring(notificationText.length() - 1)) {
                        case "S":
                            notificationS = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                        case "W":
                            notificationW = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                        case "C":
                            notificationC = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                        case "F":
                            notificationF = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                        case "J":
                            notificationJ = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                        case "N":
                            notificationN = Integer.parseInt(notificationText.substring(0, notificationText.length() - 1));
                            break;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String html = webClient.sendGetRequest(open.furaffinity.client.utilities.webClient.getBaseUrl());

        if (html != null && webClient.getLastPageLoaded()) {
            return processPageData(html);
        }

        return false;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public boolean getIsNSFWAllowed() {
        return isNSFWAllowed;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPage() {
        return userPage;
    }

    public int getNotificationS() {
        return notificationS;
    }

    public int getNotificationW() {
        return notificationW;
    }

    public int getNotificationC() {
        return notificationC;
    }

    public int getNotificationF() {
        return notificationF;
    }

    public int getNotificationJ() {
        return notificationJ;
    }

    public int getNotificationN() {
        return notificationN;
    }
}