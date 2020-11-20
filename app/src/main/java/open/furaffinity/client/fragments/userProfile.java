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

public class userProfile extends Fragment {
    private WebView userPageProfile;
    private WebView userProfile;

    private String userPageProfileData = "";
    private String userProfileData = "";

    private void getElements(View rootView) {
        userPageProfile = rootView.findViewById(R.id.userPageProfile);
        userProfile = rootView.findViewById(R.id.userProfile);
    }

    private void fetchPageData() {
        userPageProfileData = getArguments().getString(messageIds.userPageProfile_MESSAGE);
        userProfileData = getArguments().getString(messageIds.userProfile_Message);
    }

    private void updateUIElements() {
        userPageProfile.setBackgroundColor(Color.TRANSPARENT);
        userProfile.setBackgroundColor(Color.TRANSPARENT);

        userPageProfile.loadData("<font color='white'>" + userPageProfileData + "</font>", "text/html; charset=utf-8", "UTF-8");
        userProfile.loadData("<font color='white'>" + userProfileData + "</font>", "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        return rootView;
    }
}