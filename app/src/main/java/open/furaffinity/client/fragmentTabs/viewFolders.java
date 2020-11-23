package open.furaffinity.client.fragmentTabs;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.stringListAdapter;
import open.furaffinity.client.fragmentDrawersOld.user;
import open.furaffinity.client.utilities.messageIds;

public class viewFolders extends open.furaffinity.client.abstractClasses.tabFragment {
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView recyclerView;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView.Adapter<stringListAdapter.ViewHolder> mAdapter;

    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(requireActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    @Override
    protected void initPages() {

    }

    protected void fetchPageData() {
        if(getArguments() != null) {
            List<String> dataIn = getArguments().getStringArrayList(messageIds.SubmissionFolders_MESSAGE);

            for (String currentElement : dataIn) {
                HashMap<String, String> newDataElement = new HashMap<>();
                String[] splitCurrentElement = currentElement.split("\n");

                if (splitCurrentElement.length == 2) {
                    newDataElement.put("item", splitCurrentElement[0]);
                    newDataElement.put("class", user.class.getName());
                    newDataElement.put("path", splitCurrentElement[1]);
                    mDataSet.add(newDataElement);
                }
            }
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new stringListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}