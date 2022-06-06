package open.furaffinity.client.fragmentTabs;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.CommentListAdapter;
import open.furaffinity.client.dialogs.TextDialog;
import open.furaffinity.client.pages.Journal;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.pages.View;
import open.furaffinity.client.submitPages.SubmitComment;
import open.furaffinity.client.submitPages.SubmitReply;
import open.furaffinity.client.utilities.Html;
import open.furaffinity.client.utilities.MessageIds;


public class Comments extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter<CommentListAdapter.ViewHolder> mAdapter;
    @SuppressWarnings("FieldCanBeLocal") private LinearLayout controls;
    private EditText comment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private String pagePath;
    private String pageType;

    private LoginCheck loginCheck;
    private Journal journal;
    private View view;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view;
    }

    protected void getElements(android.view.View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText(getString(R.string.commentTextBox));
        comment.setShowSoftInputOnFocus(false);
        comment.setVisibility(android.view.View.GONE);

        controls.addView(comment);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        if (getArguments() != null) {
            pagePath = getArguments().getString(MessageIds.pagePath_MESSAGE);
            pageType = getArguments().getString(MessageIds.SubmissionCommentsType_MESSAGE);

            loginCheck = new LoginCheck(getActivity(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    ((CommentListAdapter) mAdapter).setLoggedIn(
                        ((LoginCheck) abstractPage).getIsLoggedIn());

                    if (((LoginCheck) abstractPage).getIsLoggedIn()) {
                        comment.setVisibility(android.view.View.VISIBLE);
                    }
                    else {
                        comment.setVisibility(android.view.View.GONE);
                    }
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    comment.setVisibility(android.view.View.GONE);
                    Toast.makeText(getActivity(), "Failed to load data for loginCheck",
                        Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new CommentListAdapter(mDataSet, getActivity(), false);
            recyclerView.setAdapter(mAdapter);

            journal = new Journal(getActivity(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    mDataSet.addAll(
                        Html.commentsToListHash(((Journal) abstractPage).getJournalComments()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for journal comments",
                        Toast.LENGTH_SHORT).show();
                }
            }, pagePath);

            view = new View(getActivity(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    mDataSet.addAll(
                        Html.commentsToListHash(((View) abstractPage).getSubmissionComments()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for view comments",
                        Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        }
        else {
            Toast.makeText(getActivity(), "Missing page path/type", Toast.LENGTH_SHORT).show();
        }
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);

            loginCheck = new LoginCheck(loginCheck);
            loginCheck.execute();

            if (pagePath != null && pageType != null) {
                switch (pageType) {
                    case "journal":
                        journal = new Journal(journal);
                        journal.execute();
                        break;
                    case "view":
                        view = new View(view);
                        view.execute();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void updateUiElementListeners(android.view.View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        comment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                comment.clearFocus();
                TextDialog textDialog = new TextDialog();

                textDialog.setListener(new TextDialog.DialogListener() {

                    @Override public void onDialogPositiveClick(DialogFragment dialog) {
                        new SubmitComment(getActivity(),
                            new AbstractPage.PageListener() {
                                @Override public void requestSucceeded(AbstractPage abstractPage) {
                                    resetRecycler();
                                    Toast.makeText(getActivity(), "Comment posted",
                                        Toast.LENGTH_SHORT).show();
                                }

                                @Override public void requestFailed(AbstractPage abstractPage) {
                                    Toast.makeText(getActivity(), "Failed to post comment",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }, pagePath, textDialog.getText()).execute();
                    }

                    @Override public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

                textDialog.setTitleText("Comment:");
                textDialog.show(getChildFragmentManager(), "comment");
            }
        });

        ((CommentListAdapter) mAdapter).setListener((replyToLink, userName) -> {
            TextDialog textDialog = new TextDialog();

            textDialog.setListener(new TextDialog.DialogListener() {

                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    new SubmitReply(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Reply posted", Toast.LENGTH_SHORT)
                                    .show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to post reply",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, replyToLink, textDialog.getText()).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            textDialog.setTitleText("Reply to: " + userName);
            textDialog.show(getChildFragmentManager(), "replyTo");
        });
    }
}