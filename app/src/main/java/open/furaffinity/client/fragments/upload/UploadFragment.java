package open.furaffinity.client.fragments.upload;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentUploadBinding;

public final class UploadFragment extends Fragment {

    private FragmentUploadBinding fragmentUploadBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentUploadBinding = FragmentUploadBinding.inflate(inflater, container, false);
        return fragmentUploadBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentUploadBinding = null;
    }
}
