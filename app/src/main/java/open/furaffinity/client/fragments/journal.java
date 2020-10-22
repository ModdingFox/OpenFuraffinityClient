package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.journalActivitySectionsPagerAdapter;
import open.furaffinity.client.utilities.webClient;

public class journal extends Fragment {
    private static final String TAG = journal.class.getName();

    private LinearLayout journalLinearLayout;
    private ImageView journalUserIcon;
    private TextView journalUserName;
    private TextView journalTitle;
    private TextView journalDate;
    private ViewPager viewPager;
    TabLayout tabs;

    private webClient webClient;
    private open.furaffinity.client.pages.journal page;

    private void getElements(View rootView) {
        journalLinearLayout = rootView.findViewById(R.id.journalLinearLayout);
        journalUserIcon = rootView.findViewById(R.id.journalUserIcon);
        journalUserName = rootView.findViewById(R.id.journalUserName);
        journalTitle = rootView.findViewById(R.id.journalTitle);
        journalDate = rootView.findViewById(R.id.journalDate);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.journal(pagePath);
    }

    private void fetchPageData() {
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    private void checkPageLoaded() {

    }

    private void updateUIElements() {
        Glide.with(this).load(page.getJournalUserIcon()).into(journalUserIcon);
        journalUserName.setText(page.getJournalUserName());
        journalTitle.setText(page.getJournalTitle());
        journalDate.setText(page.getJournalDate());
    }

    private void updateUIElementListeners() {
        journalLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setUserPath(page.getJournalUserLink());
            }
        });
    }

    private void setupViewPager() {
        journalActivitySectionsPagerAdapter sectionsPagerAdapter = new journalActivitySectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_journal, container, false);
        getElements(rootView);
        initClientAndPage(((mainActivity) getActivity()).getJournalPath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
        return rootView;
    }
}