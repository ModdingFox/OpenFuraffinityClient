package open.furaffinity.client.fragmentDrawers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.MsgOthersSectionsPagerAdapter;

public class MsgOthers extends AbstractAppFragment {
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override protected int getLayout() {
        return R.layout.fragment_msg_others;
    }

    protected void getElements(View rootView) {
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    @Override protected void initPages() {
        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");
    }

    @Override protected void fetchPageData() {

    }

    @Override protected void updateUiElements() {

    }

    @Override protected void updateUiElementListeners(View rootView) {

    }

    private void setupViewPager() {
        final MsgOthersSectionsPagerAdapter msgOthersSectionsPagerAdapter =
            new MsgOthersSectionsPagerAdapter(requireContext(), getChildFragmentManager());
        viewPager.setAdapter(msgOthersSectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager();
        return rootView;
    }
}
