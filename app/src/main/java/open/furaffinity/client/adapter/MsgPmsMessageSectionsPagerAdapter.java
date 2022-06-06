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
import open.furaffinity.client.fragmentTabs.WebViewContent;
import open.furaffinity.client.pages.MsgPmsMessage;
import open.furaffinity.client.utilities.MessageIds;

public class MsgPmsMessageSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[] {R.string.msgPmsMessageTab0};
    private final Context mContext;

    private final MsgPmsMessage msgPmsMessage;

    public MsgPmsMessageSectionsPagerAdapter(Context context, FragmentManager fragmentManager,
                                             MsgPmsMessage msgPmsMessage) {
        super(fragmentManager);
        mContext = context;
        this.msgPmsMessage = msgPmsMessage;
    }

    @Override public Fragment getItem(int position) {
        final Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                final WebViewContent newMsgPmsMessageWebViewContentFragment = new WebViewContent();
                bundle.putString(MessageIds.pagePath_MESSAGE, msgPmsMessage.getPagePath());
                bundle.putString(MessageIds.submissionDescription_MESSAGE,
                    MsgPmsMessage.class.getName());
                newMsgPmsMessageWebViewContentFragment.setArguments(bundle);
                return newMsgPmsMessageWebViewContentFragment;
            default:
                return new NotImplementedYet();
        }
    }

    @Nullable @Override public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override public int getCount() {
        return TAB_TITLES.length;
    }
}
