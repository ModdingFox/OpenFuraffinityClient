package open.furaffinity.client.fragments.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import open.furaffinity.client.R;
import open.furaffinity.client.databinding.FragmentAboutBinding;

public final class AboutFragment extends Fragment {

    private FragmentAboutBinding fragmentAboutBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        // AboutViewModel aboutViewModel = new ViewModelProvider(this).get(AboutViewModel.class);

        fragmentAboutBinding = FragmentAboutBinding.inflate(inflater, container, false);
        final View view = fragmentAboutBinding.getRoot();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        /*
         * final RecyclerView.Adapter<CommentListAdapter.ViewHolder> recyclerViewAdapter =
         * new CommentListAdapter(mDataSet, getActivity(), false);
         */

        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentAboutBinding = null;
    }
}
