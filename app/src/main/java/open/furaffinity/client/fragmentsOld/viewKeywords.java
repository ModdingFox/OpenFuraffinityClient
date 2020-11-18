package open.furaffinity.client.fragmentsOld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.stream.Collectors;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.checkboxListAdapter;
import open.furaffinity.client.utilities.messageIds;

public class viewKeywords extends Fragment {
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private LinearLayout controls;
    private Button searchButton;

    private ArrayList<String> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        searchButton = new Button(requireContext());
    }

    private void fetchPageData() {
        mDataSet = getArguments().getStringArrayList(messageIds.SubmissionTags_MESSAGE);
    }

    private void updateUIElements() {
        searchButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        searchButton.setText("Search Selected");
        controls.addView(searchButton);

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new checkboxListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    private void updateUIElementListeners(View rootView) {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setSearchQuery("@keywords " + ((checkboxListAdapter) mAdapter).getCheckedItems().stream().collect(Collectors.joining(" ")));
            }
        });
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