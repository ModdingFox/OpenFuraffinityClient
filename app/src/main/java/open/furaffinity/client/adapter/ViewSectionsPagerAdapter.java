package open.furaffinity.client.adapter;

import java.util.ArrayList;

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
import open.furaffinity.client.fragmentTabs.ViewFolders;
import open.furaffinity.client.fragmentTabs.ViewInfo;
import open.furaffinity.client.fragmentTabs.ViewKeywords;
import open.furaffinity.client.fragmentTabs.WebViewContent;
import open.furaffinity.client.pages.View;
import open.furaffinity.client.utilities.MessageIds;

public class ViewSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES =
        new int[] {R.string.viewTab0, R.string.viewTab1, R.string.viewTab2, R.string.viewTab3,
            R.string.viewTab4};
    private final Context mContext;

    private final View view;

    public ViewSectionsPagerAdapter(Context context, FragmentManager fragmentManager, View view) {
        super(fragmentManager);
        mContext = context;
        this.view = view;
    }

    @Override public Fragment getItem(int position) {
        Fragment result = new NotImplementedYet();
        final Bundle bundle = new Bundle();

        switch (position) {
            case 0:
                final WebViewContent newViewWebViewContentFragment = new WebViewContent();
                bundle.putString(MessageIds.pagePath_MESSAGE, view.getPagePath());
                bundle.putString(MessageIds.submissionDescription_MESSAGE,
                    View.class.getName());
                newViewWebViewContentFragment.setArguments(bundle);
                result = newViewWebViewContentFragment;
                break;
            case 1:
                final ViewInfo newViewInfoFragment = new ViewInfo();
                bundle.putString(MessageIds.submissionComments_MESSAGE,
                    view.getSubmissionCommentCount());
                bundle.putString(MessageIds.submissionFavorites_MESSAGE,
                    view.getSubmissionFavorites());
                bundle.putString(MessageIds.submissionViews_MESSAGE, view.getSubmissionViews());
                bundle.putString(MessageIds.submissionRating_MESSAGE, view.getSubmissionRating());
                bundle.putString(MessageIds.submissionCategory_MESSAGE,
                    view.getSubmissionCategory());
                bundle.putString(MessageIds.submissionSpecies_MESSAGE, view.getSubmissionSpecies());
                bundle.putString(MessageIds.submissionGender_MESSAGE, view.getSubmissionGender());
                bundle.putString(MessageIds.submissionDate_MESSAGE, view.getSubmissionDate());
                bundle.putString(MessageIds.submissionSize_MESSAGE, view.getSubmissionSize());
                newViewInfoFragment.setArguments(bundle);
                result = newViewInfoFragment;
                break;
            case 2:
                final ViewKeywords newViewKeywordsFragment = new ViewKeywords();
                bundle.putStringArrayList(MessageIds.SubmissionTags_MESSAGE,
                    new ArrayList<>(view.getSubmissionTags()));
                newViewKeywordsFragment.setArguments(bundle);
                result = newViewKeywordsFragment;
                break;
            case 3:
                final Comments newCommentsFragment = new Comments();
                bundle.putString(MessageIds.pagePath_MESSAGE, view.getPagePath());
                bundle.putString(MessageIds.SubmissionCommentsType_MESSAGE, "view");
                newCommentsFragment.setArguments(bundle);
                result = newCommentsFragment;
                break;
            case 4:
                final ViewFolders nevViewFoldersFragment = new ViewFolders();
                bundle.putStringArrayList(MessageIds.SubmissionFolders_MESSAGE,
                    new ArrayList<>(view.getFolderList()));
                nevViewFoldersFragment.setArguments(bundle);
                result = nevViewFoldersFragment;
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
