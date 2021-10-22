package open.furaffinity.client.fragmentTabs;

import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.abstractClasses.BaseFragment;
import open.furaffinity.client.pages.CommissionsPage;
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.pages.msgPmsMessage;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.utilities.messageIds;

public class webViewContent extends BaseFragment {
    private WebView webView;
    private String mData = "";

    private BasePage BasePage = null;

    @Override
    protected int getLayout() {
        return R.layout.fragment_web_view;
    }

    protected void getElements(View rootView) {
        webView = rootView.findViewById(R.id.webView);
    }

    @Override
    protected void initPages() {

    }

    protected void updateUIElements() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData("<font color='white'>" + mData + "</font>", "text/html; charset=utf-8", "UTF-8");
    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            String submissionDescription = getArguments().getString(messageIds.submissionDescription_MESSAGE);
            String pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);

            if (submissionDescription.equals(CommissionsPage.class.getName())) {
                BasePage = new CommissionsPage(getActivity(), new BasePage.pageListener() {
                    @Override
                    public void requestSucceeded(BasePage BasePage) {
                        mData = "<table>" + ((CommissionsPage) BasePage).getCommissionBodyBody() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(BasePage BasePage) {
                        Toast.makeText(getActivity(), "Failed to load data from commissions page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(journal.class.getName())) {
                BasePage = new open.furaffinity.client.pages.journal(getActivity(), new BasePage.pageListener() {
                    @Override
                    public void requestSucceeded(BasePage BasePage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.journal) BasePage).getJournalContent() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(BasePage BasePage) {
                        Toast.makeText(getActivity(), "Failed to load data from journal page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(view.class.getName())) {
                BasePage = new open.furaffinity.client.pages.view(getActivity(), new BasePage.pageListener() {
                    @Override
                    public void requestSucceeded(BasePage BasePage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.view) BasePage).getSubmissionDescription() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(BasePage BasePage) {
                        Toast.makeText(getActivity(), "Failed to load data from view page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(msgPmsMessage.class.getName())) {
                BasePage = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), new BasePage.pageListener() {
                    @Override
                    public void requestSucceeded(BasePage BasePage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.msgPmsMessage) BasePage).getMessageBody() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(BasePage BasePage) {
                        Toast.makeText(getActivity(), "Failed to load data from message page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            }

            if (pagePath != null && BasePage != null) {//No reason to try a request that will fail for sure
                BasePage.execute();
            }
        }
    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}