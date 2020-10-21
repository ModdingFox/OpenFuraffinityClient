package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragments.notImplementedYet;
import open.furaffinity.client.fragments.shouts;
import open.furaffinity.client.fragments.userGallery;
import open.furaffinity.client.fragments.userJournals;
import open.furaffinity.client.fragments.userProfile;
import open.furaffinity.client.fragments.watch;
import open.furaffinity.client.pages.user;
import open.furaffinity.client.utilities.messageIds;

public class userActivitySectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.userTab0, R.string.userTab1, R.string.userTab2, R.string.userTab3, R.string.userTab4, R.string.userTab5, R.string.userTab6, R.string.userTab7, R.string.userTab8};
    private final Context mContext;

    private user user;

    public userActivitySectionsPagerAdapter(Context context, FragmentManager fm, user user) {
        super(fm);
        mContext = context;
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                userProfile newUserProfileFragment = new userProfile();
                bundle.putString(messageIds.userPageProfile_MESSAGE, user.getUserPageProfile());
                bundle.putString(messageIds.userProfile_Message, user.getUserProfile());
                newUserProfileFragment.setArguments(bundle);
                return newUserProfileFragment;
            case 1:
                userGallery newUserGalleryFragment = new userGallery();
                bundle.putString(messageIds.userGalleryPath_Message, user.getUserGalleryPath());
                newUserGalleryFragment.setArguments(bundle);
                return newUserGalleryFragment;
            case 2:
                userGallery newUserScrapsFragment = new userGallery();
                bundle.putString(messageIds.userGalleryPath_Message, user.getUserScrapsPath());
                newUserScrapsFragment.setArguments(bundle);
                return newUserScrapsFragment;
            case 3:
                userGallery newUserFavoritesFragment = new userGallery();
                bundle.putString(messageIds.userGalleryPath_Message, user.getUserFavoritesPath());
                newUserFavoritesFragment.setArguments(bundle);
                return newUserFavoritesFragment;
            case 4:
                userJournals newUserJournalsFragment = new userJournals();
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserJournalsPath());
                newUserJournalsFragment.setArguments(bundle);
                return newUserJournalsFragment;
            case 5:
             /*
                open.furaffinity.client.fragments.webViewContent newUserComissionsFragment = new webViewContent();
                
                open.furaffinity.client.pages.commissions commissions = new commissions(user.getUserCommissionPath());
                open.furaffinity.client.utilities.webClient webClient = new open.furaffinity.client.utilities.webClient();
                
                try
                {
                    commissions.execute(webClient).get();
                }
                catch (ExecutionException | InterruptedException e)
                {
                    Log.e(TAG, "getItem: ", e);
                }
    
                bundle.putString(messageIds.submissionDescription_MESSAGE, "<table>" + commissions.getComissionBody() + "</table>");
                newUserComissionsFragment.setArguments(bundle);
                return newUserComissionsFragment;
              */
                return new notImplementedYet();
            case 6:
                watch newUserWatchedByFragment = new watch();
                bundle.putString(messageIds.userWatchRecent_MESSAGE, user.getUserRecentWatchers());
                bundle.putString(messageIds.userWatchesPath_MESSAGE, user.getUserWatchersPath());
                newUserWatchedByFragment.setArguments(bundle);
                return newUserWatchedByFragment;
            case 7:
                watch newUserWatchingFragment = new watch();
                bundle.putString(messageIds.userWatchRecent_MESSAGE, user.getUserRecentlyWatching());
                bundle.putString(messageIds.userWatchesPath_MESSAGE, user.getUserWatchingPath());
                newUserWatchingFragment.setArguments(bundle);
                return newUserWatchingFragment;
            case 8:
                shouts newUserShoutsFragment = new shouts();
                bundle.putString(messageIds.userShouts_MESSAGE, user.getUserShouts());
                newUserShoutsFragment.setArguments(bundle);
                return newUserShoutsFragment;
            default:
                return new notImplementedYet();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}