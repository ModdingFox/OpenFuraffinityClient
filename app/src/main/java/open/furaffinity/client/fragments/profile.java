package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.profileSectionsPagerAdapter;
import open.furaffinity.client.adapter.userSectionsPagerAdapter;

public class profile extends Fragment {

    private TabLayout tabs;
    private ViewPager viewPager;

    private void getElements(View rootView) {
        tabs = rootView.findViewById(R.id.tabs);
        viewPager = rootView.findViewById(R.id.view_pager);
    }

    private void updateUIElementListeners(View rootView) {

    }

    private void setupViewPager() {
        profileSectionsPagerAdapter profileSectionsPagerAdapter = new profileSectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(profileSectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getElements(rootView);
        updateUIElementListeners(rootView);
        setupViewPager();
        return rootView;
    }
}