package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.Comments;
import open.furaffinity.client.fragmentTabs.NotImplementedYet;
import open.furaffinity.client.fragmentTabs.WebViewContent;
import open.furaffinity.client.pages.Journal;
import open.furaffinity.client.utilities.MessageIds;

public class JournalSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.journalTab0, R.string.journalTab1};
    private final Context mContext;

    private final Journal journal;

    public JournalSectionsPagerAdapter(
        Context context,
        FragmentManager fragmentManager,
        Journal journal
    ) {
        super(fragmentManager);
        mContext = context;
        this.journal = journal;
    }

    @Override public Fragment getItem(int position) {
        final Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                final WebViewContent newJournalWebViewContentFragment = new WebViewContent();
                bundle.putString(MessageIds.pagePath_MESSAGE, journal.getPagePath());
                bundle.putString(MessageIds.submissionDescription_MESSAGE,
                    Journal.class.getName());
                newJournalWebViewContentFragment.setArguments(bundle);
                return newJournalWebViewContentFragment;
            case 1:
                final Comments newJournalCommentsFragment = new Comments();
                bundle.putString(MessageIds.pagePath_MESSAGE, journal.getPagePath());
                bundle.putString(MessageIds.SubmissionCommentsType_MESSAGE, "journal");
                newJournalCommentsFragment.setArguments(bundle);
                return newJournalCommentsFragment;
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