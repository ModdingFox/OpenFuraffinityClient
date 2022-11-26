package open.furaffinity.client.fragments.msgOthers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentMsgOthersBinding;

public final class MsgOthersFragment extends Fragment {

    private FragmentMsgOthersBinding fragmentBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentBinding = FragmentMsgOthersBinding.inflate(inflater, container, false);
        return fragmentBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentBinding = null;
    }
}
