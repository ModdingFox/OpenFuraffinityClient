package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.HistoryList;
import open.furaffinity.client.fragmentTabs.NotImplementedYet;
import open.furaffinity.client.utilities.MessageIds;

public class HistorySectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.historyTab0, R.string.historyTab1, R.string.historyTab2};
    private final Context mContext;

    public HistorySectionsPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result = new NotImplementedYet();
        final Bundle bundle = new Bundle();
        switch (position) {
            case 0:
            case 1:
            case 2:
                final HistoryList newHistoryListFragment = new HistoryList();
                bundle.putInt(MessageIds.historyListPage_MESSAGE, position);
                newHistoryListFragment.setArguments(bundle);
                result = newHistoryListFragment;
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
