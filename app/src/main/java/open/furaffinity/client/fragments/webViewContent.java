package open.furaffinity.client.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.messageIds;

public class webViewContent extends Fragment {
    private WebView webView;
    private String mData = "";

    private void getElements(View rootView) {
        webView = rootView.findViewById(R.id.webView);
    }

    private void fetchPageData() {
        mData = getArguments().getString(messageIds.submissionDescription_MESSAGE);
    }

    private void updateUIElements() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData("<font color='white'>" + mData + "</font>", "text/html; charset=utf-8", "UTF-8");
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