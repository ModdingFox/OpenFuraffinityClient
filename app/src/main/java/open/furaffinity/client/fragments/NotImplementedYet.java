package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentNotImplementedYetBinding;

public final class NotImplementedYet extends Fragment {

    private FragmentNotImplementedYetBinding fragmentNotImplementedYetBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentNotImplementedYetBinding = FragmentNotImplementedYetBinding.inflate(
            inflater,
            container,
            false
        );
        return fragmentNotImplementedYetBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentNotImplementedYetBinding = null;
    }
}
