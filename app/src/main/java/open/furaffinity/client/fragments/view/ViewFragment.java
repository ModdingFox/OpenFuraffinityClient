package open.furaffinity.client.fragments.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentViewBinding;

public final class ViewFragment extends Fragment {

    private FragmentViewBinding fragmentViewBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentViewBinding = FragmentViewBinding.inflate(inflater, container, false);
        return fragmentViewBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentViewBinding = null;
    }
}
