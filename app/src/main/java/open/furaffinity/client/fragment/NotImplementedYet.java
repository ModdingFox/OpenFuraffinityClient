package open.furaffinity.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentNotImplementedYetBinding;

public final class NotImplementedYet extends Fragment {

    private FragmentNotImplementedYetBinding binding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentNotImplementedYetBinding.inflate(inflater, container, false);
        final View root = binding.getRoot();
        return root;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
