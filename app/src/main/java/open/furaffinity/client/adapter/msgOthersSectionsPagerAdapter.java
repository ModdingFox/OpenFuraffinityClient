package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragments.msgOthersList;
import open.furaffinity.client.fragments.notImplementedYet;
import open.furaffinity.client.pages.msgOthers;
import open.furaffinity.client.utilities.messageIds;

public class msgOthersSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.msgOthersTab0, R.string.msgOthersTab1, R.string.msgOthersTab2, R.string.msgOthersTab3, R.string.msgOthersTab4};
    private final Context mContext;

    public msgOthersSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(messageIds.msgOthersType_MESSAGE, position);
        msgOthersList newMsgOthersListFragment = new msgOthersList();
        newMsgOthersListFragment.setArguments(bundle);
        return newMsgOthersListFragment;
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