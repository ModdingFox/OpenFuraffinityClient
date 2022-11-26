package open.furaffinity.client.fragments.msgSubmission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentMsgSubmissionBinding;

public final class MsgSubmissionFragment extends Fragment {

    private FragmentMsgSubmissionBinding fragmentMsgSubmissionBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentMsgSubmissionBinding = FragmentMsgSubmissionBinding.inflate(
            inflater,
            container,
            false
        );
        return fragmentMsgSubmissionBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentMsgSubmissionBinding = null;
    }
}
