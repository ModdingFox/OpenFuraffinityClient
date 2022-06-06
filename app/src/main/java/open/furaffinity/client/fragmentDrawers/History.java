package open.furaffinity.client.fragmentDrawers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.activity.MainActivity;
import open.furaffinity.client.adapter.HistorySectionsPagerAdapter;

public class History extends AbstractAppFragment {
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override protected int getLayout() {
        return R.layout.fragment_history;
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
        final HistorySectionsPagerAdapter historySectionsPagerAdapter =
            new HistorySectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(historySectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager();
        return rootView;
    }
}
