package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.pages.loginTest;
import open.furaffinity.client.utilities.messageIds;

public class shouts extends Fragment {
    private static String TAG = short.class.getName();

    RecyclerView.LayoutManager layoutManager;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private open.furaffinity.client.utilities.webClient webClient;
    private open.furaffinity.client.pages.loginTest loginTest;

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void fetchPageData() {
        webClient = new open.furaffinity.client.utilities.webClient(getContext());
        loginTest = new open.furaffinity.client.pages.loginTest();
        try {
            loginTest.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        mDataSet = open.furaffinity.client.pages.user.processShouts(getArguments().getString(messageIds.userShouts_MESSAGE));
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), loginTest.getIsLoggedIn());
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