package open.furaffinity.client.fragments.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentSearchBinding;

public final class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return fragmentSearchBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentSearchBinding = null;
    }
}
