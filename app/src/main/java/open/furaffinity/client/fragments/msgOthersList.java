package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.msgOthersListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.utilities.messageIds;

public class msgOthersList extends Fragment {
    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        int msgOthersType = getArguments().getInt(messageIds.msgOthersType_MESSAGE);
        String msgOthersData = getArguments().getString(messageIds.msgOthersData_MESSAGE);

        String actionText = null;

        switch (msgOthersType) {
            case 0:
                mDataSet = open.furaffinity.client.pages.msgOthers.processWatchNotifications(msgOthersData, "started watching you");
                break;
            case 2:
                mDataSet = open.furaffinity.client.pages.msgOthers.processShoutNotifications(msgOthersData, "left a shout");
                break;
            case 1:
                if (actionText == null) {
                    actionText = "replied to";
                }
            case 3:
                if (actionText == null) {
                    actionText = "favorited";
                }
                mDataSet = open.furaffinity.client.pages.msgOthers.processLineNotifications(msgOthersData, actionText);
                break;
            case 4:
                mDataSet = open.furaffinity.client.pages.msgOthers.processJournalNotifications(msgOthersData, "created journal");
                break;
            default:
                break;
        }
    }

    private void updateUIElements() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new msgOthersListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //we wont be loading more for now. The data should come in prepped. Just reusing the recycler view to be consistent
            }
        };

        //noinspection deprecation
        recyclerView.setOnScrollListener(endlessRecyclerViewScrollListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        getElements(rootView);
        fetchPageData();
        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
