package open.furaffinity.client.fragments.browse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentBrowseBinding;

public final class BrowseFragment extends Fragment {

    private FragmentBrowseBinding fragmentBrowseBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentBrowseBinding = FragmentBrowseBinding.inflate(inflater, container, false);
        return fragmentBrowseBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentBrowseBinding = null;
    }
}
