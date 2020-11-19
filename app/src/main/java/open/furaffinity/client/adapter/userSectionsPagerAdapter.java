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
import open.furaffinity.client.fragmentsOld.shouts;
import open.furaffinity.client.fragmentsOld.userGallery;
import open.furaffinity.client.fragmentsOld.userJournals;
import open.furaffinity.client.fragmentsOld.userProfile;
import open.furaffinity.client.fragmentsOld.watch;
import open.furaffinity.client.fragmentsMidMigration.webViewContent;
import open.furaffinity.client.pagesOld.user;
import open.furaffinity.client.utilities.messageIds;

public class userSectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = userSectionsPagerAdapter.class.getName();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.userTab0, R.string.userTab1, R.string.userTab2, R.string.userTab3, R.string.userTab4, R.string.userTab5, R.string.userTab6, R.string.userTab7, R.string.userTab8};
    private final Context mContext;

    private user user;
    private String currentPage;
    private String currentPagePath;

    public userSectionsPagerAdapter(Context context, FragmentManager fm, user user, String currentPage, String currentPagePath) {
        super(fm);
        mContext = context;
        this.user = user;
        this.currentPage = currentPage;
        this.currentPagePath = currentPagePath;
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
                if (currentPage.equals("gallery")) {
                    bundle.putString(messageIds.pagePath_MESSAGE, currentPagePath);
                } else {
                    bundle.putString(messageIds.pagePath_MESSAGE, user.getUserGalleryPath());
                }
                newUserGalleryFragment.setArguments(bundle);
                return newUserGalleryFragment;
            case 2:
                userGallery newUserScrapsFragment = new userGallery();
                if (currentPage.equals("favorites")) {
                    bundle.putString(messageIds.pagePath_MESSAGE, currentPagePath);
                } else {
                    bundle.putString(messageIds.pagePath_MESSAGE, user.getUserScrapsPath());
                }
                newUserScrapsFragment.setArguments(bundle);
                return newUserScrapsFragment;
            case 3:
                userGallery newUserFavoritesFragment = new userGallery();
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserFavoritesPath());
                newUserFavoritesFragment.setArguments(bundle);
                return newUserFavoritesFragment;
            case 4:
                userJournals newUserJournalsFragment = new userJournals();
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserJournalsPath());
                newUserJournalsFragment.setArguments(bundle);
                return newUserJournalsFragment;
            case 5:
                webViewContent newUserCommissionsFragment = new webViewContent();
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserCommissionPath());
                bundle.putString(messageIds.submissionDescription_MESSAGE, open.furaffinity.client.pages.commissions.class.getName());
                newUserCommissionsFragment.setArguments(bundle);
                return newUserCommissionsFragment;
            case 6:
                watch newUserWatchedByFragment = new watch();
                bundle.putString(messageIds.userWatchRecent_MESSAGE, user.getUserRecentWatchers());
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserWatchersPath());
                newUserWatchedByFragment.setArguments(bundle);
                return newUserWatchedByFragment;
            case 7:
                watch newUserWatchingFragment = new watch();
                bundle.putString(messageIds.userWatchRecent_MESSAGE, user.getUserRecentlyWatching());
                bundle.putString(messageIds.pagePath_MESSAGE, user.getUserWatchingPath());
                newUserWatchingFragment.setArguments(bundle);
                return newUserWatchingFragment;
            case 8:
                shouts newUserShoutsFragment = new shouts();
                bundle.putString(messageIds.pagePath_MESSAGE, user.getPagePath());
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