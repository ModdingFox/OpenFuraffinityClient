package open.furaffinity.client.fragmentTabs;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.adapter.StringListAdapter;
import open.furaffinity.client.fragmentDrawers.User;
import open.furaffinity.client.utilities.MessageIds;

public class ViewFolders extends AbstractAppFragment {
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    @SuppressWarnings("FieldCanBeLocal") private RecyclerView.Adapter<StringListAdapter.ViewHolder>
        mAdapter;

    @Override protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(requireActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    @Override protected void initPages() {

    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            List<String> dataIn =
                getArguments().getStringArrayList(MessageIds.SubmissionFolders_MESSAGE);

            for (String currentElement : dataIn) {
                HashMap<String, String> newDataElement = new HashMap<>();
                String[] splitCurrentElement = currentElement.split("\n");

                if (splitCurrentElement.length == 2) {
                    newDataElement.put("item", splitCurrentElement[0]);
                    newDataElement.put("class", User.class.getName());
                    newDataElement.put("path", splitCurrentElement[1]);
                    mDataSet.add(newDataElement);
                }
            }
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new StringListAdapter(mDataSet, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    @Override protected void updateUiElements() {

    }

    @Override protected void updateUiElementListeners(View rootView) {

    }
}