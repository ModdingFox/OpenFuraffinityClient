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
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.user;
import open.furaffinity.client.utilities.messageIds;

public class shouts extends open.furaffinity.client.abstractClasses.tabFragment {
    private RecyclerView.LayoutManager layoutManager;

    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayout controls;
    private EditText comment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<commentListAdapter.ViewHolder> mAdapter;

    @SuppressWarnings("FieldCanBeLocal")
    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.user user;

    private boolean isLoading = false;
    private String pagePath;
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

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

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            swipeRefreshLayout.setRefreshing(true);
            user = new open.furaffinity.client.pages.user(user);
            user.execute();
        }
    }

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    protected void initPages() {
        if(getArguments() != null) {
            pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);

            loginCheck = new open.furaffinity.client.pages.loginCheck(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    if (((open.furaffinity.client.pages.loginCheck) abstractPage).getIsLoggedIn()) {
                        comment.setVisibility(View.VISIBLE);
                    } else {
                        comment.setVisibility(View.GONE);
                    }

                    ((commentListAdapter) mAdapter).setLoggedIn(((open.furaffinity.client.pages.loginCheck) abstractPage).getIsLoggedIn());
                    resetRecycler();
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    Toast.makeText(getActivity(), "Failed to load data for loginCheck", Toast.LENGTH_SHORT).show();
                }
            });

            loginCheck.execute();

            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new commentListAdapter(mDataSet, getActivity(), false);
            recyclerView.setAdapter(mAdapter);

            user = new user(getActivity(), new abstractPage.pageListener() {
                @Override
                public void requestSucceeded(abstractPage abstractPage) {
                    mDataSet.addAll(open.furaffinity.client.pages.user.processShouts(user.getUserShouts()));
                    mAdapter.notifyDataSetChanged();

                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void requestFailed(abstractPage abstractPage) {
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "Failed to load data for shouts", Toast.LENGTH_SHORT).show();
                }
            }, pagePath);
        }
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
                        new open.furaffinity.client.submitPages.submitShout(getActivity(), new abstractPage.pageListener() {
                            @Override
                            public void requestSucceeded(abstractPage abstractPage) {
                                resetRecycler();
                                Toast.makeText(getActivity(), "Successfully posted shout", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void requestFailed(abstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to post shout", Toast.LENGTH_SHORT).show();
                            }
                        }, pagePath, user.getShoutKey(), user.getShoutName(), textDialog.getText()).execute();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

                textDialog.setTitleText("Shout:");
                textDialog.show(getChildFragmentManager(), "shout");
            }
        });
    }
}