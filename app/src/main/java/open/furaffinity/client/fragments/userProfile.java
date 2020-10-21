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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        WebView userPageProfile = rootView.findViewById(R.id.userPageProfile);
        WebView userProfile = rootView.findViewById(R.id.userProfile);

        userPageProfile.setBackgroundColor(Color.TRANSPARENT);
        userProfile.setBackgroundColor(Color.TRANSPARENT);

        userPageProfile.loadData("<font color='white'>" + getArguments().getString(messageIds.userPageProfile_MESSAGE) + "</font>", "text/html; charset=utf-8", "UTF-8");
        userProfile.loadData("<font color='white'>" + getArguments().getString(messageIds.userProfile_Message) + "</font>", "text/html; charset=utf-8", "UTF-8");

        return rootView;
    }
}