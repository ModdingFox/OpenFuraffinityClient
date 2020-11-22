package open.furaffinity.client.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabsNew.manageAccountSettings;
import open.furaffinity.client.fragmentTabsNew.manageBadges;
import open.furaffinity.client.fragmentTabsOld.manageUserSettings;
import open.furaffinity.client.fragmentTabsNew.notImplementedYet;
import open.furaffinity.client.fragmentTabsNew.manageAvatar;
import open.furaffinity.client.fragmentTabsOld.manageWatches;
import open.furaffinity.client.fragmentTabsNew.manageContactInfo;
import open.furaffinity.client.fragmentTabsNew.manageFolders;
import open.furaffinity.client.fragmentTabsNew.manageJournals;
import open.furaffinity.client.fragmentTabsOld.manageUserPageAndProfileInformation;
import open.furaffinity.client.fragmentTabsNew.manageShouts;
import open.furaffinity.client.fragmentTabsNew.manageSiteSettings;
import open.furaffinity.client.fragmentTabsNew.manageFavorites;
import open.furaffinity.client.fragmentTabsNew.manageSubmissions;

public class profileSectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = profileSectionsPagerAdapter.class.getName();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.profileAccountSettings, R.string.profileSiteSettings, R.string.profileUserSettings, R.string.profileUserPageAndProfile,
            R.string.profileContactInfo, R.string.profileAvatar, R.string.profileSubmissions, R.string.profileFolders,
            R.string.profileJournals, R.string.profileFavorites, R.string.profileWatches, R.string.profileShouts,
            R.string.profileBadges};
    private final Context mContext;

    public profileSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new manageAccountSettings();
            case 1:
                return new manageSiteSettings();
            case 2:
                return new manageUserSettings();
            case 3:
                return new manageUserPageAndProfileInformation();
            case 4:
                return new manageContactInfo();
            case 5:
                return new manageAvatar();
            case 6:
                return new manageSubmissions();
            case 7:
                return new manageFolders();
            case 8:
                return new manageJournals();
            case 9:
                return new manageFavorites();
            case 10:
                return new manageWatches();
            case 11:
                return new manageShouts();
            case 12:
                return new manageBadges();
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