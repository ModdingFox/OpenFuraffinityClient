package open.furaffinity.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.journalActivitySectionsPagerAdapter;
import open.furaffinity.client.pages.journal;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class journalActivity extends AppCompatActivity {
    private static final String TAG = journalActivity.class.getName();

    private LinearLayout journalLinearLayout;
    private ImageView journalUserIcon;
    private TextView journalUserName;
    private TextView journalTitle;
    private TextView journalDate;
    private ViewPager viewPager;

    private webClient webClient;
    private journal page;

    private void getElements() {
        journalLinearLayout = findViewById(R.id.journalLinearLayout);
        journalUserIcon = findViewById(R.id.journalUserIcon);
        journalUserName = findViewById(R.id.journalUserName);
        journalTitle = findViewById(R.id.journalTitle);
        journalDate = findViewById(R.id.journalDate);
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

        return result;
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this);
        page = new journal(pagePath);
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
                Intent intent = new Intent(v.getContext(), userActivity.class);
                intent.putExtra(messageIds.pagePath_MESSAGE, page.getJournalUserLink());
                v.getContext().startActivity(intent);
            }
        });
    }

    private void setupViewPager() {
        journalActivitySectionsPagerAdapter sectionsPagerAdapter = new journalActivitySectionsPagerAdapter(this, getSupportFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        getElements();
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
    }
}