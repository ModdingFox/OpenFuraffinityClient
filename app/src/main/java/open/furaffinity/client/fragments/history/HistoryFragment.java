package open.furaffinity.client.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentHistoryBinding;

public final class HistoryFragment extends Fragment {

    private FragmentHistoryBinding fragmentHistoryBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentHistoryBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        return fragmentHistoryBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentHistoryBinding = null;
    }
}
