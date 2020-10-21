package open.furaffinity.client.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.userActivitySectionsPagerAdapter;
import open.furaffinity.client.pages.user;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class userActivity extends AppCompatActivity {
    private static final String TAG = userActivity.class.getName();

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

    private String currentPage;

    private webClient webClient;
    private user page;

    private void getElements() {
        userName = findViewById(R.id.userName);
        userAccountStatus = findViewById(R.id.userAccountStatus);
        userAccountStatusLine = findViewById(R.id.userAccountStatusLine);
        userIcon = findViewById(R.id.userIcon);
        userViews = findViewById(R.id.userViews);
        userSubmissions = findViewById(R.id.userSubmissions);
        userFavs = findViewById(R.id.userFavs);
        userCommentsEarned = findViewById(R.id.userCommentsEarned);
        userCommentsMade = findViewById(R.id.userCommentsMade);
        userJournals = findViewById(R.id.userJournals);
        viewPager = findViewById(R.id.view_pager);
    }

    private String getPagePath() {
        String result = "";

        Intent intent = getIntent();
        Uri incomingPagePath = intent.getData();
        if (incomingPagePath == null) {
            result = intent.getStringExtra(messageIds.pagePath_MESSAGE);
        } else {
            result = incomingPagePath.getPath().toString();
        }

        Matcher userMatcher = Pattern.compile("\\/(user|gallery|scraps|favorites|journals|watchlist\\/to|watchlist\\/by)\\/([^\\/]+)").matcher(result);
        if (userMatcher.find()) {
            result = "/user/" + userMatcher.group(2);
        }
        currentPage = userMatcher.group(1);

        return result;
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this);
        page = new user(pagePath);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("There was an issue loading the data from FurAffinity. Returning you to where you came from.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
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

    private void updateUIElementListeners() {

    }

    private void setupViewPager() {
        userActivitySectionsPagerAdapter userActivitySectionsPagerAdapter = new userActivitySectionsPagerAdapter(this, getSupportFragmentManager(), page);
        viewPager.setAdapter(userActivitySectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getElements();
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
    }
}