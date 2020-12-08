package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.webClient;

public abstract class abstractPage extends AsyncTask<Void, Void, Boolean> {
    protected static final String TAG = abstractPage.class.getName();

    protected Context context;
    protected SharedPreferences sharedPref;

    protected webClient webClient;
    protected pageListener pageListener;

    public abstractPage(Context context, pageListener pageListener) {
        this.context = context;
        this.sharedPref = this.context.getSharedPreferences(this.context.getString(R.string.settingsFile), Context.MODE_PRIVATE);

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

    public interface pageListener {
        void requestSucceeded(abstractPage abstractPage);

        void requestFailed(abstractPage abstractPage);
    }
}
