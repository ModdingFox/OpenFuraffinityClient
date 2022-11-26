package open.furaffinity.client.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentSettingsBinding;

public final class SettingsFragment extends Fragment {

    private FragmentSettingsBinding fragmentSettingsBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return fragmentSettingsBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentSettingsBinding = null;
    }
}
