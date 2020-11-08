package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.html;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class comments extends Fragment {
    private static String TAG = comments.class.getName();

    RecyclerView.LayoutManager layoutManager;

    private LinearLayout controls;
    private EditText comment;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private String pagePath;
    private String pageType;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;
    private open.furaffinity.client.pages.journal journal;
    private open.furaffinity.client.pages.view view;

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        controls = rootView.findViewById(R.id.controls);
        comment = new EditText(getContext());
        comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        comment.setText("Comment...");
        comment.setShowSoftInputOnFocus(false);
        controls.addView(comment);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchInitialPageData() {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        mDataSet = html.commentsToListHash(getArguments().getString(messageIds.SubmissionComments_MESSAGE));
        pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);
        pageType = getArguments().getString(messageIds.SubmissionCommentsType_MESSAGE);
    }

    private void fetchPageData() {
        webClient = new webClient(this.getActivity());
        loginTest = new loginTest();
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        if(pagePath != null && pageType != null) {
            switch(pageType) {
                case "journal":
                    journal = new open.furaffinity.client.pages.journal(pagePath);
                    try {
                        journal.execute(webClient).get();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Could not load page: ", e);
                    }
                    mDataSet = html.commentsToListHash(journal.getJournalComments());
                    break;
                case "view":
                    view = new open.furaffinity.client.pages.view(pagePath);
                    try {
                        view.execute(webClient).get();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Could not load page: ", e);
                    }
                    mDataSet = html.commentsToListHash(view.getSubmissionComments());
                    break;
                default:
                    break;
            }
        }
    }

    private void updateUIElements() {
        if(loginTest.getIsLoggedIn()) {
            comment.setVisibility(View.VISIBLE);
        } else {
            comment.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), loginTest.getIsLoggedIn());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                mAdapter.notifyDataSetChanged();
                fetchPageData();
                updateUIElements();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    comment.clearFocus();
                    textDialog textDialog = new textDialog();

                    textDialog.setListener(new textDialog.dialogListener() {

                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("action", "reply");
                            params.put("reply", textDialog.getText());
                            params.put("submit", "Post+Comment");

                            try {
                                new AsyncTask<webClient, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + pagePath, params);
                                        return null;
                                    }
                                }.execute(new webClient(getContext())).get();

                                mDataSet.clear();
                                mAdapter.notifyDataSetChanged();
                                fetchPageData();
                                updateUIElements();
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e(TAG, "Could not post comment: ", e);
                            }
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    });

                    textDialog.setTitleText("Comment:");
                    textDialog.show(getChildFragmentManager(), "comment");
                }
            }
        });

        ((commentListAdapter)mAdapter).setListener(new commentListAdapter.refreshListener() {
            @Override
            public void reply(String replyToLink, String userName) {
                textDialog textDialog = new textDialog();

                textDialog.setListener(new textDialog.dialogListener() {

                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("send", "send");
                        params.put("reply", textDialog.getText());
                        params.put("submit", "Reply");

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + replyToLink, params);
                                    return null;
                                }
                            }.execute(new webClient(getContext())).get();

                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not post reply: ", e);
                        }

                        mDataSet.clear();
                        mAdapter.notifyDataSetChanged();
                        fetchPageData();
                        updateUIElements();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

                textDialog.setTitleText("Reply to: " + userName);
                textDialog.show(getChildFragmentManager(), "replyTo");
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
        getElements(rootView);
        fetchInitialPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}