package open.furaffinity.client.fragmentDrawersNew;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.historySectionsPagerAdapter;

public class history extends open.furaffinity.client.abstractClasses.tabFragment {
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected int getLayout() {
        return R.layout.fragment_history;
    }

    protected void getElements(View rootView) {
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    @Override
    protected void initPages() {

    }

    @Override
    protected void fetchPageData() {

    }

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }

    private void setupViewPager() {
        historySectionsPagerAdapter historySectionsPagerAdapter = new historySectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(historySectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager();
        return rootView;
    }
}
