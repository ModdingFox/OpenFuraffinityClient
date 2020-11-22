package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabsNew.comments;
import open.furaffinity.client.fragmentTabsOld.notImplementedYet;
import open.furaffinity.client.fragmentTabsOld.webViewContent;
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.utilities.messageIds;

public class journalSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.journalTab0, R.string.journalTab1};
    private final Context mContext;

    private journal journal;

    public journalSectionsPagerAdapter(Context context, FragmentManager fm, journal journal) {
        super(fm);
        mContext = context;
        this.journal = journal;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                webViewContent newJournalWebViewContentFragment = new webViewContent();
                bundle.putString(messageIds.pagePath_MESSAGE, journal.getPagePath());
                bundle.putString(messageIds.submissionDescription_MESSAGE, open.furaffinity.client.pages.journal.class.getName());
                newJournalWebViewContentFragment.setArguments(bundle);
                return newJournalWebViewContentFragment;
            case 1:
                comments newJournalCommentsFragment = new comments();
                bundle.putString(messageIds.pagePath_MESSAGE, journal.getPagePath());
                bundle.putString(messageIds.SubmissionCommentsType_MESSAGE, "journal");
                newJournalCommentsFragment.setArguments(bundle);
                return newJournalCommentsFragment;
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