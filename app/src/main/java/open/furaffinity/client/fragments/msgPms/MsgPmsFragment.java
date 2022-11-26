package open.furaffinity.client.fragments.msgPms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import open.furaffinity.client.databinding.FragmentMsgPmsBinding;

public final class MsgPmsFragment extends Fragment {

    private FragmentMsgPmsBinding fragmentMsgPmsBinding;

    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        fragmentMsgPmsBinding = FragmentMsgPmsBinding.inflate(inflater, container, false);
        return fragmentMsgPmsBinding.getRoot();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        fragmentMsgPmsBinding = null;
    }
}
