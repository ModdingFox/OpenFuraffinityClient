package open.furaffinity.client.abstractClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import open.furaffinity.client.R;
import open.furaffinity.client.utilities.WebClient;

public abstract class AbstractPage extends AsyncTask<Void, Void, Boolean> {
    protected static final String TAG = AbstractPage.class.getName();

    protected Context context;
    protected SharedPreferences sharedPref;

    protected WebClient webClient;
    protected PageListener pageListener;

    public AbstractPage(Context context, PageListener pageListener) {
        this.context = context;
        this.sharedPref =
            this.context.getSharedPreferences(this.context.getString(R.string.settingsFile),
                Context.MODE_PRIVATE);

        this.webClient = new WebClient(context);
        this.pageListener = pageListener;
    }

    public AbstractPage(AbstractPage abstractPage) {
        this.context = abstractPage.context;
        this.webClient = abstractPage.webClient;
        this.pageListener = abstractPage.pageListener;
    }

    protected abstract Boolean processPageData(String html);

    @Override protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (aBoolean) {
            this.pageListener.requestSucceeded(AbstractPage.this);
        }
        else {
            this.pageListener.requestFailed(AbstractPage.this);
        }
    }

    public interface PageListener {
        void requestSucceeded(AbstractPage abstractPage);

        void requestFailed(AbstractPage abstractPage);
    }
}
