package open.furaffinity.client.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragmentsOld.notImplementedYet;

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
                return new open.furaffinity.client.fragmentsOld.manageAccountSettings();
            case 1:
                return new open.furaffinity.client.fragmentsOld.manageSiteSettings();
            case 2:
                return new open.furaffinity.client.fragmentsOld.manageUserSettings();
            case 3:
                return new open.furaffinity.client.fragmentsOld.manageUserPageAndProfileInformation();
            case 4:
                return new open.furaffinity.client.fragmentsOld.manageContactInfo();
            case 5:
                return new open.furaffinity.client.fragmentsOld.manageAvatar();
            case 6:
                return new open.furaffinity.client.fragmentsOld.manageSubmissions();
            case 7:
                return new open.furaffinity.client.fragmentsOld.manageFolders();
            case 8:
                return new open.furaffinity.client.fragmentsOld.manageJournals();
            case 9:
                return new open.furaffinity.client.fragmentsOld.manageFavorites();
            case 10:
                return new open.furaffinity.client.fragmentsOld.manageWatches();
            case 11:
                return new open.furaffinity.client.fragmentsOld.manageShouts();
            case 12:
                return new open.furaffinity.client.fragmentsOld.manageBadges();
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