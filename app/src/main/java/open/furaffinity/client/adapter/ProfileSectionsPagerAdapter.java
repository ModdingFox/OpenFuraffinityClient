package open.furaffinity.client.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.ManageAccountSettings;
import open.furaffinity.client.fragmentTabs.ManageAvatar;
import open.furaffinity.client.fragmentTabs.ManageBadges;
import open.furaffinity.client.fragmentTabs.ManageContactInfo;
import open.furaffinity.client.fragmentTabs.ManageFavorites;
import open.furaffinity.client.fragmentTabs.ManageFolders;
import open.furaffinity.client.fragmentTabs.ManageJournals;
import open.furaffinity.client.fragmentTabs.ManageShouts;
import open.furaffinity.client.fragmentTabs.ManageSiteSettings;
import open.furaffinity.client.fragmentTabs.ManageSubmissions;
import open.furaffinity.client.fragmentTabs.ManageUserPageAndProfileInformation;
import open.furaffinity.client.fragmentTabs.ManageUserSettings;
import open.furaffinity.client.fragmentTabs.ManageWatches;
import open.furaffinity.client.fragmentTabs.NotImplementedYet;

public class ProfileSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.profileAccountSettings, R.string.profileSiteSettings,
            R.string.profileUserSettings, R.string.profileUserPageAndProfile,
            R.string.profileContactInfo, R.string.profileAvatar, R.string.profileSubmissions,
            R.string.profileFolders, R.string.profileJournals, R.string.profileFavorites,
            R.string.profileWatches, R.string.profileShouts, R.string.profileBadges};
    private final Context mContext;

    public ProfileSectionsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override public Fragment getItem(int position) {
        Fragment result = new NotImplementedYet();
        switch (position) {
            case 0:
                result = new ManageAccountSettings();
                break;
            case 1:
                result = new ManageSiteSettings();
                break;
            case 2:
                result = new ManageUserSettings();
                break;
            case 3:
                result = new ManageUserPageAndProfileInformation();
                break;
            case 4:
                result = new ManageContactInfo();
                break;
            case 5:
                result = new ManageAvatar();
                break;
            case 6:
                result = new ManageSubmissions();
                break;
            case 7:
                result = new ManageFolders();
                break;
            case 8:
                result = new ManageJournals();
                break;
            case 9:
                result = new ManageFavorites();
                break;
            case 10:
                result = new ManageWatches();
                break;
            case 11:
                result = new ManageShouts();
                break;
            case 12:
                result = new ManageBadges();
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
