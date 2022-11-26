package open.furaffinity.client.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentProfileBinding;

public final class ProfileFragment extends Fragment {

    private FragmentProfileBinding fragmentProfileBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return fragmentProfileBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentProfileBinding = null;
    }
}
