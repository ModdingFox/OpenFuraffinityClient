package open.furaffinity.client.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.userActivitySectionsPagerAdapter;
import open.furaffinity.client.utilities.webClient;

public class user extends Fragment {
    private static final String TAG = user.class.getName();

    private TextView userName;
    private TextView userAccountStatus;
    private TextView userAccountStatusLine;
    private ImageView userIcon;
    private TextView userViews;
    private TextView userSubmissions;
    private TextView userFavs;
    private TextView userCommentsEarned;
    private TextView userCommentsMade;
    private TextView userJournals;
    ViewPager viewPager;
    TabLayout tabs;

    private String currentPage;

    private webClient webClient;
    private open.furaffinity.client.pages.user page;

    private void getElements(View rootView) {
        userName = rootView.findViewById(R.id.userName);
        userAccountStatus = rootView.findViewById(R.id.userAccountStatus);
        userAccountStatusLine = rootView.findViewById(R.id.userAccountStatusLine);
        userIcon = rootView.findViewById(R.id.userIcon);
        userViews = rootView.findViewById(R.id.userViews);
        userSubmissions = rootView.findViewById(R.id.userSubmissions);
        userFavs = rootView.findViewById(R.id.userFavs);
        userCommentsEarned = rootView.findViewById(R.id.userCommentsEarned);
        userCommentsMade = rootView.findViewById(R.id.userCommentsMade);
        userJournals = rootView.findViewById(R.id.userJournals);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    private String getPagePath() {
        String result = ((mainActivity)getActivity()).getUserPath();

        Matcher userMatcher = Pattern.compile("\\/(user|gallery|scraps|favorites|journals|commissions|watchlist\\/to|watchlist\\/by)\\/([^\\/]+)").matcher(result);
        if (userMatcher.find()) {
            result = "/user/" + userMatcher.group(2);
        }
        currentPage = userMatcher.group(1);

        return result;
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.user(pagePath);
    }

    private void fetchPageData() {
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }
    }

    private void checkPageLoaded() {
        if (!page.getIsLoaded()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage("There was an issue loading the data from FurAffinity. Returning you to where you came from.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void updateUIElements() {
        userName.setText(page.getUserName());
        userAccountStatus.setText(page.getUserAccountStatus());
        userAccountStatusLine.setText(page.getUserAccountStatusLine());
        Glide.with(this).load(page.getUserIcon()).into(userIcon);
        userViews.setText(page.getUserViews());
        userSubmissions.setText(page.getUserSubmissions());
        userFavs.setText(page.getUserFavs());
        userCommentsEarned.setText(page.getUserCommentsEarned());
        userCommentsMade.setText(page.getUserCommentsMade());
        userJournals.setText(page.getUserJournals());
    }

    private void updateUIElementListeners(View rootView) {

    }

    private void setupViewPager() {
        userActivitySectionsPagerAdapter userActivitySectionsPagerAdapter = new userActivitySectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(userActivitySectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);

        switch (currentPage) {
            case "user":
                viewPager.setCurrentItem(0);
                break;
            case "gallery":
                viewPager.setCurrentItem(1);
                break;
            case "scraps":
                viewPager.setCurrentItem(2);
                break;
            case "favorites":
                viewPager.setCurrentItem(3);
                break;
            case "journals":
                viewPager.setCurrentItem(4);
                break;
            case "commissions":
                viewPager.setCurrentItem(5);
                break;
            case "watchlist/to":
                viewPager.setCurrentItem(6);
                break;
            case "watchlist/by":
                viewPager.setCurrentItem(7);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        getElements(rootView);
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners(rootView);
        setupViewPager();
        return rootView;
    }
}