package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.os.AsyncTask;

import open.furaffinity.client.utilities.webClient;

public abstract class abstractPage extends AsyncTask<Void, Void, Boolean> {
    protected static final String TAG = abstractPage.class.getName();

    protected Context context;
    protected webClient webClient;

    public interface pageListener {
        public void requestSucceeded(abstractPage abstractPage);

        public void requestFailed(abstractPage abstractPage);
    }

    protected pageListener pageListener;

    public abstractPage(Context context, pageListener pageListener) {
        this.context = context;
        this.webClient = new webClient(context);
        this.pageListener = pageListener;
    }

    public abstractPage(abstractPage abstractPage) {
        this.context = abstractPage.context;
        this.webClient = abstractPage.webClient;
        this.pageListener = abstractPage.pageListener;
    }

    protected abstract Boolean processPageData(String html);

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            this.pageListener.requestSucceeded(abstractPage.this);
        } else {
            this.pageListener.requestFailed(abstractPage.this);
        }
    }
}
