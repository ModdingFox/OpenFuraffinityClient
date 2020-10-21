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
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.utilities.messageIds;

public class shouts extends Fragment {
    RecyclerView.LayoutManager layoutManager;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        mDataSet = open.furaffinity.client.pages.user.processShouts(getArguments().getString(messageIds.userShouts_MESSAGE));
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
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
        return rootView;
    }
}