package open.furaffinity.client.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import open.furaffinity.client.R;
import open.furaffinity.client.fragmentTabs.comments;
import open.furaffinity.client.fragmentTabs.notImplementedYet;
import open.furaffinity.client.fragmentTabs.viewFolders;
import open.furaffinity.client.fragmentTabs.viewInfo;
import open.furaffinity.client.fragmentTabs.viewKeywords;
import open.furaffinity.client.fragmentTabs.webViewContent;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.utilities.messageIds;

public class viewSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.viewTab0, R.string.viewTab1, R.string.viewTab2, R.string.viewTab3, R.string.viewTab4};
    private final Context mContext;

    private final view view;

    public viewSectionsPagerAdapter(Context context, FragmentManager fm, view view) {
        super(fm);
        mContext = context;
        this.view = view;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                webViewContent newViewWebViewContentFragment = new webViewContent();
                bundle.putString(messageIds.pagePath_MESSAGE, view.getPagePath());
                bundle.putString(messageIds.submissionDescription_MESSAGE, open.furaffinity.client.pages.view.class.getName());
                newViewWebViewContentFragment.setArguments(bundle);
                return newViewWebViewContentFragment;
            case 1:
                viewInfo newViewInfoFragment = new viewInfo();
                bundle.putString(messageIds.submissionComments_MESSAGE, view.getSubmissionCommentCount());
                bundle.putString(messageIds.submissionFavorites_MESSAGE, view.getSubmissionFavorites());
                bundle.putString(messageIds.submissionViews_MESSAGE, view.getSubmissionViews());
                bundle.putString(messageIds.submissionRating_MESSAGE, view.getSubmissionRating());
                bundle.putString(messageIds.submissionCategory_MESSAGE, view.getSubmissionCategory());
                bundle.putString(messageIds.submissionSpecies_MESSAGE, view.getSubmissionSpecies());
                bundle.putString(messageIds.submissionGender_MESSAGE, view.getSubmissionGender());
                bundle.putString(messageIds.submissionDate_MESSAGE, view.getSubmissionDate());
                bundle.putString(messageIds.submissionSize_MESSAGE, view.getSubmissionSize());
                newViewInfoFragment.setArguments(bundle);
                return newViewInfoFragment;
            case 2:
                viewKeywords newViewKeywordsFragment = new viewKeywords();
                bundle.putStringArrayList(messageIds.SubmissionTags_MESSAGE, new ArrayList<>(view.getSubmissionTags()));
                newViewKeywordsFragment.setArguments(bundle);
                return newViewKeywordsFragment;
            case 3:
                comments newCommentsFragment = new comments();
                bundle.putString(messageIds.pagePath_MESSAGE, view.getPagePath());
                bundle.putString(messageIds.SubmissionCommentsType_MESSAGE, "view");
                newCommentsFragment.setArguments(bundle);
                return newCommentsFragment;
            case 4:
                viewFolders nevViewFoldersFragment = new viewFolders();
                bundle.putStringArrayList(messageIds.SubmissionFolders_MESSAGE, new ArrayList<>(view.getFolderList()));
                nevViewFoldersFragment.setArguments(bundle);
                return nevViewFoldersFragment;
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