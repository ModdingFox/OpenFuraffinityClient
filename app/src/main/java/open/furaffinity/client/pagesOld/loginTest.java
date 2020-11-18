package open.furaffinity.client.pagesOld;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import open.furaffinity.client.utilities.webClient;

public class loginTest extends AsyncTask<webClient, Void, Void> {
    private boolean isLoggedIn = false;
    private boolean isNSFWAllowed = false;
    private String userIcon = "";
    private String userName = "";

    private void checkLoginStatus(String html) {
        Document doc = Jsoup.parse(html);
        if (doc != null && doc.selectFirst("a[href=/login]") == null) {
            isLoggedIn = true;

            Element nsfwToggle = doc.selectFirst("input.slider-toggle[id=sfw-toggle-mobile]");

            if (nsfwToggle != null) {
                isNSFWAllowed = true;
            }

            Element userIconImg = doc.selectFirst("img.loggedin_user_avatar");
            open.furaffinity.client.utilities.html.correctHtmlAHrefAndImgScr(userIconImg);

            userIcon = userIconImg.attr("src");
            userName = userIconImg.attr("alt");
        }
    }


    public loginTest() {
    }

    public loginTest(String html) {
        checkLoginStatus(html);
    }

    @Override
    protected Void doInBackground(webClient... webClients) {
        checkLoginStatus(webClients[0].sendGetRequest(webClients[0].getBaseUrl()));
        return null;
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
};