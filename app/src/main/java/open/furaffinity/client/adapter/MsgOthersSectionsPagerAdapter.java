package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.MsgOthersList;
import open.furaffinity.client.utilities.MessageIds;

public class MsgOthersSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.msgOthersTab0, R.string.msgOthersTab1, R.string.msgOthersTab2,
            R.string.msgOthersTab3, R.string.msgOthersTab4, R.string.msgOthersTab5};
    private final Context mContext;

    public MsgOthersSectionsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override public Fragment getItem(int position) {
        final Bundle bundle = new Bundle();
        bundle.putInt(MessageIds.msgOthersType_MESSAGE, position);
        final MsgOthersList newMsgOthersListFragment = new MsgOthersList();
        newMsgOthersListFragment.setArguments(bundle);
        return newMsgOthersListFragment;
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override public int getCount() {
        return TAB_TITLES.length;
    }
}
