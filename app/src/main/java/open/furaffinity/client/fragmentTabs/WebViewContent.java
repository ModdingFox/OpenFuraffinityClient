package open.furaffinity.client.fragmentTabs;

import android.graphics.Color;
import android.webkit.WebView;
import android.widget.Toast;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.pages.Commissions;
import open.furaffinity.client.pages.Journal;
import open.furaffinity.client.pages.MsgPmsMessage;
import open.furaffinity.client.pages.View;
import open.furaffinity.client.utilities.MessageIds;

public class WebViewContent extends AbstractAppFragment {
    private WebView webView;
    private String mData = "";

    private AbstractPage abstractPage = null;

    @Override protected int getLayout() {
        return R.layout.fragment_web_view;
    }

    protected void getElements(android.view.View rootView) {
        webView = rootView.findViewById(R.id.webView);
    }

    @Override protected void initPages() {

    }

    protected void updateUiElements() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData("<font color='white'>" + mData + "</font>", "text/html; charset=utf-8",
            "UTF-8");
    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            String submissionDescription =
                getArguments().getString(MessageIds.submissionDescription_MESSAGE);
            String pagePath = getArguments().getString(MessageIds.pagePath_MESSAGE);

            if (submissionDescription.equals(Commissions.class.getName())) {
                abstractPage = new Commissions(getActivity(),
                    new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            mData = "<table>" +
                                ((Commissions) abstractPage).getCommissionBodyBody() +
                                "</table>";
                            updateUiElements();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(),
                                    "Failed to load data from commissions page", Toast.LENGTH_SHORT)
                                .show();
                        }
                    }, pagePath);
            }
            else if (submissionDescription.equals(Journal.class.getName())) {
                abstractPage = new Journal(getActivity(),
                    new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            mData = "<table>" +
                                ((Journal) abstractPage).getJournalContent() +
                                "</table>";
                            updateUiElements();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to load data from journal page",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, pagePath);
            }
            else if (submissionDescription.equals(View.class.getName())) {
                abstractPage = new View(getActivity(),
                    new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            mData = "<table>" +
                                ((View) abstractPage).getSubmissionDescription() +
                                "</table>";
                            updateUiElements();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to load data from view page",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, pagePath);
            }
            else if (submissionDescription.equals(MsgPmsMessage.class.getName())) {
                abstractPage = new MsgPmsMessage(getActivity(),
                    new AbstractPage.PageListener() {
                        @Override public void requestSucceeded(AbstractPage abstractPage) {
                            mData = "<table>" +
                                ((MsgPmsMessage) abstractPage).getMessageBody() +
                                "</table>";
                            updateUiElements();
                        }

                        @Override public void requestFailed(AbstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to load data from message page",
                                Toast.LENGTH_SHORT).show();
                        }
                    }, pagePath);
            }

            if (pagePath != null &&
                abstractPage != null) {//No reason to try a request that will fail for sure
                abstractPage.execute();
            }
        }
    }

    @Override protected void updateUiElementListeners(android.view.View rootView) {

    }
}