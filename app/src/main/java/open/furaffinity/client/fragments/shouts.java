package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.gallery;
import open.furaffinity.client.pages.user;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class shouts extends Fragment {
    private static String TAG = short.class.getName();

    private RecyclerView.LayoutManager layoutManager;

    private LinearLayout controls;
    private EditText comment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.user user;

    private boolean isLoading = false;
    private String pagePath;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText("Comment...");
        comment.setShowSoftInputOnFocus(false);
        comment.setVisibility(View.GONE);

        controls.addView(comment);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
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

    private void initPages() {
        loginCheck = new open.furaffinity.client.pages.loginCheck(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((open.furaffinity.client.pages.loginCheck)abstractPage).getIsLoggedIn()) {
                    comment.setVisibility(View.VISIBLE);
                } else {
                    comment.setVisibility(View.GONE);
                }

                ((commentListAdapter)mAdapter).setLoggedIn(((open.furaffinity.client.pages.loginCheck)abstractPage).getIsLoggedIn());
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

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetRecycler();
            }
        });

        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    comment.clearFocus();
                    textDialog textDialog = new textDialog();

                    textDialog.setListener(new textDialog.dialogListener() {

                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("action", "shout");
                            params.put("key", user.getShoutKey());
                            params.put("name", user.getShoutName());
                            params.put("shout", textDialog.getText());
                            params.put("submit", "Submit");

                            try {
                                new AsyncTask<open.furaffinity.client.utilities.webClient, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                                        return null;
                                    }
                                }.execute(new webClient(getContext())).get();

                                resetRecycler();
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e(TAG, "Could not post shout: ", e);
                            }
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    });

                    textDialog.setTitleText("Shout:");
                    textDialog.show(getChildFragmentManager(), "shout");
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view, container, false);
        pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}