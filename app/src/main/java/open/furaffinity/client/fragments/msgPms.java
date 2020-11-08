package open.furaffinity.client.fragments;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.msgPmsListAdapter;
import open.furaffinity.client.dialogs.spinnerDialog;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class msgPms extends Fragment {
    private static final String TAG = open.furaffinity.client.fragments.msgPms.class.getName();

    private ConstraintLayout constraintLayout;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

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

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        fab = rootView.findViewById(R.id.fab);

        newMessage = new FloatingActionButton(getContext());
        deleteSelectedMessages = new FloatingActionButton(getContext());
        setSelectedMessagesPriority = new FloatingActionButton(getContext());
        messageListOptions = new FloatingActionButton(getContext());

        newMessage.setImageResource(R.drawable.ic_menu_newmessage);
        deleteSelectedMessages.setImageResource(R.drawable.ic_menu_delete);
        setSelectedMessagesPriority.setImageResource(R.drawable.ic_menu_inbox);
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

    private void updateUIElementListeners(View rootView) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                ((msgPmsListAdapter)mAdapter).clearChecked();
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
            sendPM(getActivity(), getChildFragmentManager(), null);
        });

        deleteSelectedMessages.setOnClickListener(view ->
        {
            List<String> itemIds = ((msgPmsListAdapter)mAdapter).getCheckedItems();

            HashMap<String, String> params = new HashMap<>();
            params.put("manage_notes", "1");
            params.put("move_to", "trash");

            for(int i = 0; i < itemIds.size(); i++) {
                params.put("items[" + Integer.toString(i) + "]", itemIds.get(i));
            }

            try {
                new AsyncTask<webClient, Void, Void>() {
                    @Override
                    protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                        webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath(), params);
                        return null;
                    }
                }.execute(webClient).get();

                recyclerView.scrollTo(0, 0);
                mDataSet.clear();
                ((msgPmsListAdapter)mAdapter).clearChecked();
                mAdapter.notifyDataSetChanged();
                endlessRecyclerViewScrollListener.resetState();

                page.setPage(1);

                page = new open.furaffinity.client.pages.msgPms(page);
                fetchPageData();
                updateUIElements();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Could not remove notification: ", e);
            }
        });

        setSelectedMessagesPriority.setOnClickListener(view ->
        {
            HashMap<String, String> prioritiesList = new HashMap<>();
            for (open.furaffinity.client.pages.msgPms.priorities currentPriority : open.furaffinity.client.pages.msgPms.priorities.values()) {
                prioritiesList.put(currentPriority.toString(), currentPriority.getPrintableName());
            }

            spinnerDialog spinnerDialog = new spinnerDialog();
            spinnerDialog.setTitleText("Select Priority");
            spinnerDialog.setData(prioritiesList);
            spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<String> itemIds = ((msgPmsListAdapter)mAdapter).getCheckedItems();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("manage_notes", "1");

                    if(spinnerDialog.getSpinnerSelection().equals(open.furaffinity.client.pages.msgPms.priorities.archive.toString())) {
                        params.put("move_to", spinnerDialog.getSpinnerSelection());
                    } else {
                        params.put("set_prio", spinnerDialog.getSpinnerSelection());
                    }

                    for(int i = 0; i < itemIds.size(); i++) {
                        params.put("items[" + Integer.toString(i) + "]", itemIds.get(i));
                    }

                    try {
                        new AsyncTask<webClient, Void, Void>() {
                            @Override
                            protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + page.getPagePath(), params);
                                return null;
                            }
                        }.execute(webClient).get();

                        recyclerView.scrollTo(0, 0);
                        mDataSet.clear();
                        ((msgPmsListAdapter)mAdapter).clearChecked();
                        mAdapter.notifyDataSetChanged();
                        endlessRecyclerViewScrollListener.resetState();

                        page.setPage(1);

                        page = new open.furaffinity.client.pages.msgPms(page);
                        fetchPageData();
                        updateUIElements();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Could not remove notification: ", e);
                    }

                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });

        messageListOptions.setOnClickListener(view ->
        {
            HashMap<String, String> foldersList = new HashMap<>();
            for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
                foldersList.put(currentMailFolder.toString(), currentMailFolder.getPrintableName());
            }

            spinnerDialog spinnerDialog = new spinnerDialog();
            spinnerDialog.setTitleText("Select Folder");
            spinnerDialog.setData(foldersList);
            spinnerDialog.setListener(new spinnerDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    String selectedFolderValue = spinnerDialog.getSpinnerSelection();
                    if (!page.getSelectedFolder().toString().equals(selectedFolderValue)) {
                        HashMap<String, open.furaffinity.client.pages.msgPms.mailFolders> foldersList = new HashMap<>();
                        for (open.furaffinity.client.pages.msgPms.mailFolders currentMailFolder : open.furaffinity.client.pages.msgPms.mailFolders.values()) {
                            foldersList.put(currentMailFolder.toString(), currentMailFolder);
                        }

                        page.setSelectedFolder(foldersList.get(selectedFolderValue));

                        recyclerView.scrollTo(0, 0);
                        mDataSet.clear();
                        ((msgPmsListAdapter)mAdapter).clearChecked();
                        mAdapter.notifyDataSetChanged();
                        endlessRecyclerViewScrollListener.resetState();

                        page.setPage(1);

                        page = new open.furaffinity.client.pages.msgPms(page);
                        fetchPageData();
                        updateUIElements();
                    }
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            spinnerDialog.show(getChildFragmentManager(), "selectPriority");
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refreshable_recycler_view_with_fab, container, false);
        getElements(rootView);
        initClientAndPage();
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
