package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragments.notImplementedYet;
import open.furaffinity.client.fragments.webViewContent;
import open.furaffinity.client.pagesOld.msgPmsMessage;
import open.furaffinity.client.utilities.messageIds;

public class msgPmsMessageSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.msgPmsMessageTab0};
    private final Context mContext;

    private msgPmsMessage msgPmsMessage;

    public msgPmsMessageSectionsPagerAdapter(Context context, FragmentManager fm, msgPmsMessage msgPmsMessage) {
        super(fm);
        mContext = context;
        this.msgPmsMessage = msgPmsMessage;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                webViewContent newMsgPmsMessageWebViewContentFragment = new webViewContent();
                bundle.putString(messageIds.submissionDescription_MESSAGE, msgPmsMessage.getMessageBody());
                newMsgPmsMessageWebViewContentFragment.setArguments(bundle);
                return newMsgPmsMessageWebViewContentFragment;
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