package open.furaffinity.client.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentUserBinding;

public final class UserFragment extends Fragment {

    private FragmentUserBinding fragmentUserBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentUserBinding = FragmentUserBinding.inflate(inflater, container, false);
        return fragmentUserBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentUserBinding = null;
    }
}
