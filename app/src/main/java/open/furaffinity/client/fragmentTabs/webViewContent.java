package open.furaffinity.client.fragmentTabs;

import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.commissions;
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.pages.msgPmsMessage;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.utilities.messageIds;

public class webViewContent extends open.furaffinity.client.abstractClasses.tabFragment {
    private WebView webView;
    private String mData = "";

    private open.furaffinity.client.abstractClasses.abstractPage abstractPage = null;

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

    private void updateUIElements() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData("<font color='white'>" + mData + "</font>", "text/html; charset=utf-8", "UTF-8");
    }

    protected void fetchPageData() {
        if(getArguments() != null) {
            String submissionDescription = getArguments().getString(messageIds.submissionDescription_MESSAGE);
            String pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);

            if (submissionDescription.equals(commissions.class.getName())) {
                abstractPage = new open.furaffinity.client.pages.commissions(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.commissions) abstractPage).getCommissionBodyBody() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data from commissions page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(journal.class.getName())) {
                abstractPage = new open.furaffinity.client.pages.journal(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.journal) abstractPage).getJournalContent() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data from journal page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(view.class.getName())) {
                abstractPage = new open.furaffinity.client.pages.view(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.view) abstractPage).getSubmissionDescription() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data from view page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            } else if (submissionDescription.equals(msgPmsMessage.class.getName())) {
                abstractPage = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        mData = "<table>" + ((open.furaffinity.client.pages.msgPmsMessage) abstractPage).getMessageBody() + "</table>";
                        updateUIElements();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data from message page", Toast.LENGTH_SHORT).show();
                    }
                }, pagePath);
            }

            if (pagePath != null && abstractPage != null) {//No reason to try a request that will fail for sure
                abstractPage.execute();
            }
        }
    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}