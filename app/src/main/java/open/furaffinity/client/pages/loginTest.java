package open.furaffinity.client.pages;

import android.os.AsyncTask;

import org.jsoup.nodes.Document;

import open.furaffinity.client.utilities.webClient;

public class loginTest extends AsyncTask<webClient, Void, Void> {
    private boolean isLoggedIn = false;

    @Override
    protected Void doInBackground(webClient... webClients) {
        Document doc = new Document(webClients[0].sendGetRequest(webClients[0].getBaseUrl()));
        if(doc.selectFirst("a[href=/login]") == null) { isLoggedIn = true; }
        return null;
    }

    public boolean getIsLoggedIn() { return isLoggedIn; }
};