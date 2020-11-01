package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.msgPmsListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class msgPms extends Fragment {
    private static final String TAG = open.furaffinity.client.fragments.msgPms.class.getName();

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private Spinner msgPmsFolderSpinner;

    private FloatingActionButton fab;

    private webClient webClient;
    private open.furaffinity.client.pages.msgPms page;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        msgPmsFolderSpinner = rootView.findViewById(R.id.msgPmsFolderSpinner);

        fab = rootView.findViewById(R.id.fab);
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
//        recyclerView.setHasFixedSize(true);
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

        fab.setOnClickListener(view ->
        {
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            TableLayout settingsTableLayout = rootView.findViewById(R.id.settingsTableLayout);
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
                loadCurrentSettings();
                settingsTableLayout.setVisibility(View.VISIBLE);
            } else {
                settingsTableLayout.setVisibility(View.GONE);
                saveCurrentSettings();
                recyclerView.setVisibility(View.VISIBLE);
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
