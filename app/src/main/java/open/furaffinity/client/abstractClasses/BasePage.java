package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.webClient;

public abstract class BasePage extends AsyncTask<Void, Void, Boolean> {
    protected static final String TAG = BasePage.class.getName();

    protected Context context;
    protected SharedPreferences sharedPref;

    protected webClient webClient;
    protected pageListener pageListener;

    public BasePage(Context context, pageListener pageListener) {
        this.context = context;
        this.sharedPref = this.context.getSharedPreferences(this.context.getString(R.string.settingsFile), Context.MODE_PRIVATE);

        this.webClient = new webClient(context);
        this.pageListener = pageListener;
    }

    public BasePage(BasePage BasePage) {
        this.context = BasePage.context;
        this.webClient = BasePage.webClient;
        this.pageListener = BasePage.pageListener;
    }

    protected abstract Boolean processPageData(String html);

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            this.pageListener.requestSucceeded(BasePage.this);
        } else {
            this.pageListener.requestFailed(BasePage.this);
        }
    }

    public interface pageListener {
        void requestSucceeded(BasePage BasePage);

        void requestFailed(BasePage BasePage);
    }
}
