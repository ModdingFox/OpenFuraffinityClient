package open.furaffinity.client.fragmentTabs;

import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.utilities.MessageIds;

public class UserProfile extends AbstractAppFragment {
    private WebView userPageProfile;
    private WebView userProfile;

    private String userPageProfileData = "";
    private String userProfileData = "";

    @Override protected int getLayout() {
        return R.layout.fragment_user_profile;
    }

    protected void getElements(View rootView) {
        userPageProfile = rootView.findViewById(R.id.userPageProfile);
        userProfile = rootView.findViewById(R.id.userProfile);
    }

    @Override protected void initPages() {

    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            userPageProfileData = getArguments().getString(MessageIds.userPageProfile_MESSAGE);
            userProfileData = getArguments().getString(MessageIds.userProfile_Message);
        }

        userPageProfile.setBackgroundColor(Color.TRANSPARENT);
        userProfile.setBackgroundColor(Color.TRANSPARENT);

        userPageProfile.loadData("<font color='white'>" + userPageProfileData + "</font>",
            "text/html; charset=utf-8", "UTF-8");
        userProfile.loadData("<font color='white'>" + userProfileData + "</font>",
            "text/html; charset=utf-8", "UTF-8");
    }

    @Override protected void updateUiElements() {

    }

    @Override protected void updateUiElementListeners(View rootView) {

    }
}