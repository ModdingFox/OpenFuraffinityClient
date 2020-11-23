package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.checkboxListAdapter;
import open.furaffinity.client.utilities.messageIds;

public class viewKeywords extends appFragment {
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<checkboxListAdapter.ViewHolder> mAdapter;

    private LinearLayout controls;
    private Button searchButton;

    private ArrayList<String> mDataSet = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        searchButton = new Button(requireContext());
    }

    @Override
    protected void initPages() {
        searchButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        searchButton.setText(getString(R.string.searchSelected));
        controls.addView(searchButton);
    }

    protected void fetchPageData() {
        if(getArguments() != null) {
            mDataSet = getArguments().getStringArrayList(messageIds.SubmissionTags_MESSAGE);
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new checkboxListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    @Override
    protected void updateUIElements() {

    }

    protected void updateUIElementListeners(View rootView) {
        searchButton.setOnClickListener(v -> ((mainActivity) requireActivity()).setSearchQuery("@keywords " + String.join(" ", ((checkboxListAdapter) mAdapter).getCheckedItems())));
    }
}