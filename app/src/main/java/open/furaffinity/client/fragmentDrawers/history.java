package open.furaffinity.client.fragmentDrawers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.historySectionsPagerAdapter;

public class history extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabs;

    private void getElements(View rootView) {
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    private void setupViewPager() {
        historySectionsPagerAdapter historySectionsPagerAdapter = new historySectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(historySectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        getElements(rootView);
        setupViewPager();
        return rootView;
    }
}
