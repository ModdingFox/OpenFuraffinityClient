package open.furaffinity.client.fragmentsOld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.msgOthersSectionsPagerAdapter;

public class msgOthers extends Fragment {
    private static final String TAG = msgOthers.class.getName();

    private ViewPager viewPager;

    private void getElements(View rootView) {
        viewPager = rootView.findViewById(R.id.view_pager);
    }

    private void setupViewPager(View rootView) {
        msgOthersSectionsPagerAdapter msgOthersSectionsPagerAdapter = new msgOthersSectionsPagerAdapter(rootView.getContext(), getChildFragmentManager());
        viewPager.setAdapter(msgOthersSectionsPagerAdapter);
        TabLayout tabs = rootView.findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_others, container, false);
        getElements(rootView);
        setupViewPager(rootView);
        return rootView;
    }
}
