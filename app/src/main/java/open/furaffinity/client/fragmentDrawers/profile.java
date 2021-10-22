package open.furaffinity.client.fragmentDrawers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.profileSectionsPagerAdapter;

public class profile extends appFragment {

    private TabLayout tabs;
    private ViewPager viewPager;

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile;
    }

    protected void getElements(View rootView) {
        tabs = rootView.findViewById(R.id.tabs);
        viewPager = rootView.findViewById(R.id.view_pager);
    }

    @Override
    protected void initPages() {
        ((mainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");
    }

    @Override
    protected void fetchPageData() {

    }

    @Override
    protected void updateUIElements() {

    }

    protected void updateUIElementListeners(View rootView) {

    }

    private void setupViewPager() {
        profileSectionsPagerAdapter profileSectionsPagerAdapter = new profileSectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(profileSectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager();
        return rootView;
    }
}