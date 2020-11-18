package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.os.AsyncTask;

import open.furaffinity.client.utilities.webClient;

public abstract class page extends AsyncTask<Void, Void, Boolean> {
    protected static final String TAG = page.class.getName();

    protected Context context;
    protected webClient webClient;

    public interface pageListener {
        public void requestSucceeded();

        public void requestFailed();
    }

    protected pageListener pageListener;

    public page(Context context, pageListener pageListener) {
        this.context = context;
        this.webClient = new webClient(context);
        this.pageListener = pageListener;
    }

    public page(page page) {
        this.context = page.context;
        this.webClient = page.webClient;
        this.pageListener = page.pageListener;
    }

    protected abstract Boolean processPageData(String html);

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            this.pageListener.requestSucceeded();
        } else {
            this.pageListener.requestFailed();
        }
    }
}
