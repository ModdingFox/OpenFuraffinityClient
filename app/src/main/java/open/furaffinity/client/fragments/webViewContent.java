package open.furaffinity.client.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.utilities.messageIds;

public class webViewContent extends Fragment {
    private WebView webView;
    private String mData = "";

    private open.furaffinity.client.abstractClasses.abstractPage abstractPage = null;

    private void getElements(View rootView) {
        webView = rootView.findViewById(R.id.webView);
    }

    private void updateUIElements() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData("<font color='white'>" + mData + "</font>", "text/html; charset=utf-8", "UTF-8");
    }

    private void fetchPageData() {
        String submissionDescription = getArguments().getString(messageIds.submissionDescription_MESSAGE);
        String pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);

        if(submissionDescription == open.furaffinity.client.pages.commissions.class.getName()) {
            abstractPage = new open.furaffinity.client.pages.commissions(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mData = "<table>" + ((open.furaffinity.client.pages.commissions)abstractPage).getCommissionBodyBody() + "</table>";
                    updateUIElements();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data from commissions page", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        } else if(submissionDescription == open.furaffinity.client.pages.journal.class.getName()) {
            abstractPage = new open.furaffinity.client.pages.journal(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mData = "<table>" + ((open.furaffinity.client.pages.journal)abstractPage).getJournalContent() + "</table>";
                    updateUIElements();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data from journal page", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        } else if(submissionDescription == open.furaffinity.client.pages.view.class.getName()) {
            abstractPage = new open.furaffinity.client.pages.view(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mData = "<table>" + ((open.furaffinity.client.pages.view)abstractPage).getSubmissionDescription() + "</table>";
                    updateUIElements();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data from view page", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        } else if (submissionDescription == open.furaffinity.client.pages.msgPmsMessage.class.getName()) {
            abstractPage = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mData = "<table>" + ((open.furaffinity.client.pages.msgPmsMessage)abstractPage).getMessageBody() + "</table>";
                    updateUIElements();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data from message page", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        }

        if(pagePath != null && abstractPage != null) {//No reason to try a request that will fail for sure
            abstractPage.execute();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        return rootView;
    }
}