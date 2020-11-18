package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragmentsOld.historyList;
import open.furaffinity.client.fragmentsOld.notImplementedYet;
import open.furaffinity.client.utilities.messageIds;

public class historySectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.historyTab0, R.string.historyTab1, R.string.historyTab2};
    private final Context mContext;

    public historySectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
            case 1:
            case 2:
                historyList newHistoryListFragment = new historyList();
                bundle.putInt(messageIds.historyListPage_MESSAGE, position);
                newHistoryListFragment.setArguments(bundle);
                return newHistoryListFragment;
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