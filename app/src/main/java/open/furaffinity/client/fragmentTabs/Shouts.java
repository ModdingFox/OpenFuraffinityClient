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
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.CommentListAdapter;
import open.furaffinity.client.dialogs.TextDialog;
import open.furaffinity.client.pages.LoginCheck;
import open.furaffinity.client.pages.User;
import open.furaffinity.client.submitPages.SubmitShout;
import open.furaffinity.client.utilities.MessageIds;

public class Shouts extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    @SuppressWarnings("FieldCanBeLocal") private LinearLayout controls;
    private EditText comment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<CommentListAdapter.ViewHolder> mAdapter;
    @SuppressWarnings("FieldCanBeLocal") private LoginCheck
        loginCheck;
    private User user;
    private boolean isLoading = false;
    private String pagePath;

    @Override protected int getLayout() {
        return R.layout.fragment_refreshable_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText(getString(R.string.commentTextBox));
        comment.setShowSoftInputOnFocus(false);
        comment.setVisibility(View.GONE);

        controls.addView(comment);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            user = new User(user);
            user.execute();
        }
    }

    @Override protected void updateUiElements() {

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

            loginCheck = new LoginCheck(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        if (((LoginCheck) abstractPage).getIsLoggedIn()) {
                            comment.setVisibility(View.VISIBLE);
                        }
                        else {
                            comment.setVisibility(View.GONE);
                        }

                        ((CommentListAdapter) mAdapter).setLoggedIn(
                            ((LoginCheck) abstractPage).getIsLoggedIn());
                        resetRecycler();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data for loginCheck",
                            Toast.LENGTH_SHORT).show();
                    }
                });

            loginCheck.execute();

            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new CommentListAdapter(mDataSet, getActivity(), false);
            recyclerView.setAdapter(mAdapter);

            user = new User(getActivity(), new AbstractPage.PageListener() {
                @Override public void requestSucceeded(AbstractPage abstractPage) {
                    mDataSet.addAll(
                        User.processShouts(user.getUserShouts()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override public void requestFailed(AbstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for shouts",
                        Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        }
    }

    protected void updateUiElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(this::resetRecycler);

        comment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                comment.clearFocus();
                TextDialog textDialog = new TextDialog();

                textDialog.setListener(new TextDialog.DialogListener() {

                    @Override public void onDialogPositiveClick(DialogFragment dialog) {
                        new SubmitShout(getActivity(),
                            new AbstractPage.PageListener() {
                                @Override public void requestSucceeded(AbstractPage abstractPage) {
                                    resetRecycler();
                                    Toast.makeText(getActivity(), "Successfully posted shout",
                                        Toast.LENGTH_SHORT).show();
                                }

                                @Override public void requestFailed(AbstractPage abstractPage) {
                                    Toast.makeText(getActivity(), "Failed to post shout",
                                        Toast.LENGTH_SHORT).show();
                                }
                            }, pagePath, user.getShoutKey(), user.getShoutName(),
                            textDialog.getText()).execute();
                    }

                    @Override public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

                textDialog.setTitleText("Shout:");
                textDialog.show(getChildFragmentManager(), "shout");
            }
        });
    }
}