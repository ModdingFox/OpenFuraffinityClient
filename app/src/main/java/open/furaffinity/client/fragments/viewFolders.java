package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.checkboxListAdapter;
import open.furaffinity.client.adapter.stringListAdapter;
import open.furaffinity.client.utilities.messageIds;

public class viewFolders extends Fragment {
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private LinearLayout controls;

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
    }

    private void fetchPageData() {
        List<String> dataIn = getArguments().getStringArrayList(messageIds.SubmissionFolders_MESSAGE);

        for(String currentElement : dataIn) {
            HashMap<String, String> newDataElement = new HashMap<>();
            String [] splitCurrentElement = currentElement.split("\n");

            if(splitCurrentElement.length == 2) {
                newDataElement.put("item", splitCurrentElement[0]);
                newDataElement.put("class", open.furaffinity.client.fragments.user.class.getName());
                newDataElement.put("path", splitCurrentElement[1]);
                mDataSet.add(newDataElement);
            }
        }
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new stringListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {

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