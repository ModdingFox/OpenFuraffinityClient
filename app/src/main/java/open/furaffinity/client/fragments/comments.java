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
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.pages.loginCheck;
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

    private boolean isLoading = false;
    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private String pagePath;
    private String pageType;

    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.loginCheck loginCheck;
    private open.furaffinity.client.pages.journal journal;
    private open.furaffinity.client.pages.view view;

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

    private void resetRecycler() {
        recyclerView.scrollTo(0, 0);
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        fetchPageData();
    }

    private void initPages() {
        webClient = new webClient(this.getActivity());

        loginCheck = new loginCheck(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                ((commentListAdapter)mAdapter).setLoggedIn(((loginCheck)abstractPage).getIsLoggedIn());

                if (((loginCheck)abstractPage).getIsLoggedIn()) {
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
                mDataSet.addAll(html.commentsToListHash(((journal)abstractPage).getJournalComments()));
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
                mDataSet.addAll(html.commentsToListHash(((view)abstractPage).getSubmissionComments()));
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
    }

    private void fetchPageData() {
        if(!isLoading) {
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

                                resetRecycler();
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

        ((commentListAdapter) mAdapter).setListener(new commentListAdapter.refreshListener() {
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

                        resetRecycler();
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
        pagePath = getArguments().getString(messageIds.pagePath_MESSAGE);
        pageType = getArguments().getString(messageIds.SubmissionCommentsType_MESSAGE);

        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}