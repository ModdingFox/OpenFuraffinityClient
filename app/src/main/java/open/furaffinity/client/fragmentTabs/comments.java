package open.furaffinity.client.fragmentTabs;

import android.view.View;
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
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.pages.loginCheck;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.utilities.html;
import open.furaffinity.client.utilities.messageIds;


public class comments extends appFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter<commentListAdapter.ViewHolder> mAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayout controls;
    private EditText comment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private String pagePath;
    private String pageType;

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.journal journal;
    private open.furaffinity.client.pages.view view;

    @Override
    protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText(getString(R.string.commentTextBox));
        comment.setShowSoftInputOnFocus(false);
        comment.setVisibility(View.GONE);

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
            pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);
            pageType = getArguments().getString(messageIds.SubmissionCommentsType_MESSAGE);

            loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    ((commentListAdapter) mAdapter).setLoggedIn(((loginCheck) abstractPage).getIsLoggedIn());

                    if (((loginCheck) abstractPage).getIsLoggedIn()) {
                        comment.setVisibility(View.VISIBLE);
                    } else {
                        comment.setVisibility(View.GONE);
                    }
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    comment.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new commentListAdapter(mDataSet, getActivity(), false);
            recyclerView.setAdapter(mAdapter);

            journal = new journal(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mDataSet.addAll(html.commentsToListHash(((journal) abstractPage).getJournalComments()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for journal comments", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);

            view = new view(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mDataSet.addAll(html.commentsToListHash(((view) abstractPage).getSubmissionComments()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for view comments", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        } else {
            Toast.makeText(getActivity(), "Missing page path/type", Toast.LENGTH_SHORT).show();
        }
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);

            loginCheck = new loginCheck(loginCheck);
            loginCheck.execute();

            if (pagePath != null && pageType != null) {
                switch (pageType) {
                    case "journal":
                        journal = new journal(journal);
                        journal.execute();
                        break;
                    case "view":
                        view = new view(view);
                        view.execute();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void updateUIElements() {

    }

    protected void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        comment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                comment.clearFocus();
                textDialog textDialog = new textDialog();

                textDialog.setListener(new textDialog.dialogListener() {

                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        new open.furaffinity.client.submitPages.submitComment(getActivity(), new abstractPage.pageListener() {
                            @Override
                            public void requestSucceeded(abstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Comment posted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void requestFailed(abstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to post comment", Toast.LENGTH_SHORT).show();
                            }
                        }, pagePath, textDialog.getText()).execute();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

                textDialog.setTitleText("Comment:");
                textDialog.show(getChildFragmentManager(), "comment");
            }
        });

        ((commentListAdapter) mAdapter).setListener((replyToLink, userName) -> {
            textDialog textDialog = new textDialog();

            textDialog.setListener(new textDialog.dialogListener() {

                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    new open.furaffinity.client.submitPages.submitReply(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            resetRecycler();
                            Toast.makeText(getActivity(), "Reply posted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to post reply", Toast.LENGTH_SHORT).show();
                        }
                    }, replyToLink, textDialog.getText()).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });

            textDialog.setTitleText("Reply to: " + userName);
            textDialog.show(getChildFragmentManager(), "replyTo");
        });
    }
}