package open.furaffinity.client.pages;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.abstractClasses.abstractPage;

public class loginCheck extends abstractPage {
    private boolean isLoggedIn = false;
    private boolean isNSFWAllowed = false;
    private String userIcon = "";
    private String userName = "";

    public loginCheck(Context context, pageListener pageListener) {
        super(context, pageListener);
    }

    public loginCheck(loginCheck loginCheck) {
        super(loginCheck);
        this.isLoggedIn = loginCheck.isLoggedIn;
        this.isNSFWAllowed = loginCheck.isNSFWAllowed;
        this.userIcon = loginCheck.userIcon;
        this.userName = loginCheck.userName;
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
}