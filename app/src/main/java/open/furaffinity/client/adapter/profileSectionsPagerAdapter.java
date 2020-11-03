package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.fragments.notImplementedYet;
import open.furaffinity.client.fragments.shouts;
import open.furaffinity.client.fragments.userGallery;
import open.furaffinity.client.fragments.userJournals;
import open.furaffinity.client.fragments.userProfile;
import open.furaffinity.client.fragments.watch;
import open.furaffinity.client.fragments.webViewContent;
import open.furaffinity.client.pages.commissions;
import open.furaffinity.client.pages.user;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class profileSectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = profileSectionsPagerAdapter.class.getName();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{ R.string.profileAccountSettings, R.string.profileSiteSettings, R.string.profileUserSettings, R.string.profileUserPageAndProfile,
            R.string.profileContactInfo, R.string.profileAvatar, R.string.profileSubmissions, R.string.profileFolders,
            R.string.profileJournals, R.string.profileFavorites, R.string.profileWatches, R.string.profileShouts,
            R.string.profileBadges };
    private final Context mContext;

    public profileSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new open.furaffinity.client.fragments.manageAccountSettings();
            case 1:
                return new open.furaffinity.client.fragments.manageSiteSettings();
            case 2:
                return new open.furaffinity.client.fragments.manageUserSettings();
            case 3:
                return new open.furaffinity.client.fragments.manageUserPageAndProfileInformation();
            case 4:
                return new open.furaffinity.client.fragments.manageContactInfo();
            case 5:
                return new open.furaffinity.client.fragments.manageAvatar();
            case 6:
                return new open.furaffinity.client.fragments.manageSubmissions();
            case 7:
                return new open.furaffinity.client.fragments.manageFolders();
            case 8:
                return new open.furaffinity.client.fragments.manageJournals();
            case 9:
                return new open.furaffinity.client.fragments.manageFavorites();
            case 10:
                return new open.furaffinity.client.fragments.manageWatches();
            case 11:
                return new open.furaffinity.client.fragments.manageShouts();
            case 12:
                return new open.furaffinity.client.fragments.manageBadges();
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