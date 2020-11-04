package open.furaffinity.client.fragments;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.manageImageListAdapter;
import open.furaffinity.client.adapter.msgPmsListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class msgPms extends Fragment {
    private static final String TAG = open.furaffinity.client.fragments.msgPms.class.getName();

    private ConstraintLayout constraintLayout;
    private TableLayout settingsTableLayout;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private Spinner msgPmsFolderSpinner;

    private fabCircular fab;
    private FloatingActionButton newMessage;
    private FloatingActionButton deleteSelectedMessages;
    private FloatingActionButton setSelectedMessagesPriority;
    private FloatingActionButton messageListOptions;

    private webClient webClient;
    private open.furaffinity.client.pages.msgPms page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        constraintLayout = rootView.findViewById(R.id.constraintLayout);
        settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        msgPmsFolderSpinner = rootView.findViewById(R.id.msgPmsFolderSpinner);

        fab = rootView.findViewById(R.id.fab);

        newMessage = new FloatingActionButton(getContext());
        deleteSelectedMessages = new FloatingActionButton(getContext());
        setSelectedMessagesPriority = new FloatingActionButton(getContext());
        messageListOptions = new FloatingActionButton(getContext());

        newMessage.setImageResource(R.drawable.ic_menu_newmessage);
        deleteSelectedMessages.setImageResource(R.drawable.ic_menu_inbox);
        setSelectedMessagesPriority.setImageResource(R.drawable.ic_menu_delete);
        messageListOptions.setImageResource(R.drawable.ic_menu_settings);

        newMessage.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        deleteSelectedMessages.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        setSelectedMessagesPriority.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));
        messageListOptions.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        constraintLayout.addView(newMessage);
        constraintLayout.addView(deleteSelectedMessages);
        constraintLayout.addView(setSelectedMessagesPriority);
        constraintLayout.addView(messageListOptions);

        fab.addButton(deleteSelectedMessages, 1.5f, 180);
        fab.addButton(setSelectedMessagesPriority, 1.5f, 225);
        fab.addButton(newMessage, 1.5f, 270);
        fab.addButton(messageListOptions, 2.625f, 270);
    }

    private void initClientAndPage() {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.msgPms();
    }

    private void fetchPageData() {
        page = new open.furaffinity.client.pages.msgPms(page);
        try {
            page.execute(webClient).get();
        }//we wait to get the data here. Fuck if i know the proper way to do this in android
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "loadNextPage: ", e);
        }

        List<HashMap<String, String>> messages = page.getMessages();

        //Deduplicate results
        List<String> newMessages = messages.stream().map(currentMap -> currentMap.get("messageid")).collect(Collectors.toList());
        List<String> oldMessages = mDataSet.stream().map(currentMap -> currentMap.get("messageid")).collect(Collectors.toList());
        newMessages.removeAll(oldMessages);
        messages = messages.stream().filter(currentMap -> newMessages.contains(currentMap.get("messageid"))).collect(Collectors.toList());
        mDataSet.addAll(messages);
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new msgPmsListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void loadCurrentSettings() {
        HashMap<String, String> foldersList = new HashMap<>();
        for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
            foldersList.put(currentMailFolder.toString(), currentMailFolder.getPrintableName());
        }
        uiControls.spinnerSetAdapter(requireContext(), msgPmsFolderSpinner, foldersList, page.getSelectedFolder().toString(), true, false);
    }

    private void saveCurrentSettings() {
        boolean valueChanged = false;

        String selectedFolderValue = ((kvPair) msgPmsFolderSpinner.getSelectedItem()).getKey();
        if (!page.getSelectedFolder().toString().equals(selectedFolderValue)) {
            HashMap<String, open.furaffinity.client.pages.msgPms.mailFolders> foldersList = new HashMap<>();
            for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
                foldersList.put(currentMailFolder.toString(), currentMailFolder);
            }

            page.setSelectedFolder(foldersList.get(selectedFolderValue));
            valueChanged = true;
        }

        if (valueChanged) {
            recyclerView.scrollTo(0, 0);
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
            endlessRecyclerViewScrollListener.resetState();

            page.setPage(1);

            page = new open.furaffinity.client.pages.msgPms(page);
            fetchPageData();
        }
    }

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
//                ((manageImageListAdapter)mAdapter).clearChecked();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();

                page.setPage(1);

                page = new open.furaffinity.client.pages.msgPms(page);
                fetchPageData();
                updateUIElements();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int pageNumber, int totalItemsCount, RecyclerView view) {
                page.setPage(page.getPage() + 1);
                int curSize = mAdapter.getItemCount();
                fetchPageData();
                mAdapter.notifyItemRangeInserted(curSize, mDataSet.size() - 1);
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);

        newMessage.setOnClickListener(view ->
        {
            msgPmsDialog msgPmsDialog = new msgPmsDialog();
            msgPmsDialog.setListener((user, subject, body) -> {
                recaptchaV2Dialog recaptchaV2Dialog = new recaptchaV2Dialog();
                recaptchaV2Dialog.setPagePath(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath());
                recaptchaV2Dialog.setListener(new recaptchaV2Dialog.recaptchaV2DialogListener() {
                    @Override
                    public void gRecaptchaResponseFound(String gRecaptchaResponse) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("key", page.getPostKey());
                        params.put("to", user);
                        params.put("subject", subject);
                        params.put("message", body);
                        params.put("g-recaptcha-response", gRecaptchaResponse);

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + open.furaffinity.client.pages.msgPms.getSendPath(), params);
                                    return null;
                                }
                            }.execute(webClient).get();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not reply to message: ", e);
                        }
                    }
                });
                recaptchaV2Dialog.show(getChildFragmentManager(), "recaptchaV2");
            });

            msgPmsDialog.show(getChildFragmentManager(), "msgPmsDialog");
        });

        deleteSelectedMessages.setOnClickListener(view ->
        {

        });

        setSelectedMessagesPriority.setOnClickListener(view ->
        {

        });

        messageListOptions.setOnClickListener(view ->
        {
            if (swipeRefreshLayout.getVisibility() == View.VISIBLE) {
                swipeRefreshLayout.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            } else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_pms, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
