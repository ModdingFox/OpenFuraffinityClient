package open.furaffinity.client.fragmentTabs;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.CheckboxListAdapter;
import open.furaffinity.client.sqlite.SearchContract;
import open.furaffinity.client.utilities.MessageIds;

public class ViewKeywords extends AbstractAppFragment {
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<CheckboxListAdapter.ViewHolder> mAdapter;

    private LinearLayout controls;
    private Button searchButton;

    private ArrayList<String> mDataSet = new ArrayList<>();

    @Override protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);

        controls = rootView.findViewById(R.id.controls);
        searchButton = new Button(requireContext());
    }

    @Override protected void initPages() {
        searchButton.setLayoutParams(
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        searchButton.setText(getString(R.string.searchSelected));
        controls.addView(searchButton);
    }

    protected void fetchPageData() {
        if (getArguments() != null) {
            mDataSet = getArguments().getStringArrayList(MessageIds.SubmissionTags_MESSAGE);
        }

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CheckboxListAdapter(mDataSet);
        recyclerView.setAdapter(mAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }

    @Override protected void updateUiElements() {

    }

    protected void updateUiElementListeners(View rootView) {
        searchButton.setOnClickListener(v -> {
            JSONObject searchQuery = new JSONObject();

            try {
                searchQuery.put(SearchContract.searchItemEntry.COLUMN_NAME_Q, ("@keywords " +
                    String.join(" ", ((CheckboxListAdapter) mAdapter).getCheckedItems())));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((MainActivity) requireActivity()).setSearchParamaters(searchQuery.toString());
        });
    }
}