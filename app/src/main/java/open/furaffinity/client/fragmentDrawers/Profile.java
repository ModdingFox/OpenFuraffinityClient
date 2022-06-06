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
import open.furaffinity.client.adapter.ProfileSectionsPagerAdapter;

public class Profile extends AbstractAppFragment {

    private TabLayout tabs;
    private ViewPager viewPager;

    @Override protected int getLayout() {
        return R.layout.fragment_profile;
    }

    protected void getElements(View rootView) {
        tabs = rootView.findViewById(R.id.tabs);
        viewPager = rootView.findViewById(R.id.view_pager);
    }

    @Override protected void initPages() {
        ((MainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(), "");
    }

    @Override protected void fetchPageData() {

    }

    @Override protected void updateUiElements() {

    }

    protected void updateUiElementListeners(View rootView) {

    }

    private void setupViewPager() {
        final ProfileSectionsPagerAdapter profileSectionsPagerAdapter =
            new ProfileSectionsPagerAdapter(this.getActivity(), getChildFragmentManager());
        viewPager.setAdapter(profileSectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager();
        return rootView;
    }
}