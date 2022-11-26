package open.furaffinity.client.fragments.journal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentJournalBinding;

public final class JournalFragment extends Fragment {

    private FragmentJournalBinding fragmentJournalBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentJournalBinding = FragmentJournalBinding.inflate(inflater, container, false);
        return fragmentJournalBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentJournalBinding = null;
    }
}
