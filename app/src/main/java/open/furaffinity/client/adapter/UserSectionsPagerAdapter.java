package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.NotImplementedYet;
import open.furaffinity.client.fragmentTabs.Shouts;
import open.furaffinity.client.fragmentTabs.UserGallery;
import open.furaffinity.client.fragmentTabs.UserJournals;
import open.furaffinity.client.fragmentTabs.UserProfile;
import open.furaffinity.client.fragmentTabs.Watch;
import open.furaffinity.client.fragmentTabs.WebViewContent;
import open.furaffinity.client.pages.Commissions;
import open.furaffinity.client.pages.User;
import open.furaffinity.client.utilities.MessageIds;

public class UserSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.userTab0, R.string.userTab1, R.string.userTab2, R.string.userTab3,
            R.string.userTab4, R.string.userTab5, R.string.userTab6, R.string.userTab7,
            R.string.userTab8};
    private final Context mContext;

    private final User user;
    private final String currentPage;
    private final String currentPagePath;

    public UserSectionsPagerAdapter(Context context, FragmentManager fragmentManager, User user,
                                    String currentPage, String currentPagePath) {
        super(fragmentManager);
        mContext = context;
        this.user = user;
        this.currentPage = currentPage;
        this.currentPagePath = currentPagePath;
    }

    @Override public Fragment getItem(int position) {
        Fragment result = new NotImplementedYet();
        final Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                final UserProfile newUserProfileFragment = new UserProfile();
                bundle.putString(MessageIds.userPageProfile_MESSAGE, user.getUserPageProfile());
                bundle.putString(MessageIds.userProfile_Message, user.getUserProfile());
                newUserProfileFragment.setArguments(bundle);
                result = newUserProfileFragment;
                break;
            case 1:
                final UserGallery newUserGalleryFragment = new UserGallery();
                if (currentPage.equals("gallery")) {
                    bundle.putString(MessageIds.pagePath_MESSAGE, currentPagePath);
                }
                else {
                    bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserGalleryPath());
                }
                newUserGalleryFragment.setArguments(bundle);
                result = newUserGalleryFragment;
                break;
            case 2:
                final UserGallery newUserScrapsFragment = new UserGallery();
                if (currentPage.equals("favorites")) {
                    bundle.putString(MessageIds.pagePath_MESSAGE, currentPagePath);
                }
                else {
                    bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserScrapsPath());
                }
                newUserScrapsFragment.setArguments(bundle);
                result = newUserScrapsFragment;
                break;
            case 3:
                final UserGallery newUserFavoritesFragment = new UserGallery();
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserFavoritesPath());
                newUserFavoritesFragment.setArguments(bundle);
                result = newUserFavoritesFragment;
                break;
            case 4:
                final UserJournals newUserJournalsFragment = new UserJournals();
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserJournalsPath());
                newUserJournalsFragment.setArguments(bundle);
                result = newUserJournalsFragment;
                break;
            case 5:
                final WebViewContent newUserCommissionsFragment = new WebViewContent();
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserCommissionPath());
                bundle.putString(MessageIds.submissionDescription_MESSAGE,
                    Commissions.class.getName());
                newUserCommissionsFragment.setArguments(bundle);
                result = newUserCommissionsFragment;
                break;
            case 6:
                final Watch newUserWatchedByFragment = new Watch();
                bundle.putString(MessageIds.userWatchRecent_MESSAGE, user.getUserRecentWatchers());
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserWatchersPath());
                newUserWatchedByFragment.setArguments(bundle);
                result = newUserWatchedByFragment;
                break;
            case 7:
                final Watch newUserWatchingFragment = new Watch();
                bundle.putString(MessageIds.userWatchRecent_MESSAGE,
                    user.getUserRecentlyWatching());
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getUserWatchingPath());
                newUserWatchingFragment.setArguments(bundle);
                result = newUserWatchingFragment;
                break;
            case 8:
                final Shouts newUserShoutsFragment = new Shouts();
                bundle.putString(MessageIds.pagePath_MESSAGE, user.getPagePath());
                newUserShoutsFragment.setArguments(bundle);
                result = newUserShoutsFragment;
                break;
            default:
                break;
        }
        return result;
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override public int getCount() {
        return TAB_TITLES.length;
    }
}
