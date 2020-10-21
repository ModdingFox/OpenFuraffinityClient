package open.furaffinity.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.msgPmsMessageActivitySectionsPagerAdapter;
import open.furaffinity.client.pages.msgPmsMessage;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class msgPmsMessageActivity extends AppCompatActivity {
    private static final String TAG = msgPmsMessageActivity.class.getName();

    private TextView subject;
    private ImageView userIcon;
    private TextView sentBy;
    //private TextView sentTo;
    private TextView sentDate;
    private WebView webView;
    private ViewPager viewPager;

    private webClient webClient;
    private msgPmsMessage page;

    private void getElements() {
        subject = findViewById(R.id.subject);
        userIcon = findViewById(R.id.userIcon);
        sentBy = findViewById(R.id.sentBy);
        //sentTo = findViewById(R.id.sentTo);
        sentDate = findViewById(R.id.sentDate);
        webView = findViewById(R.id.webView);
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
        page = new msgPmsMessage(pagePath);
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
        subject.setText(page.getMessageSubject());
        Glide.with(this).load(page.getMessageUserIcon()).into(userIcon);
        sentBy.setText(page.getMessageSentBy());
        //sentTo.setText(page.getMessageSentTo());
        sentDate.setText(page.getMessageSentDate());
    }

    private void updateUIElementListeners() {
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), open.furaffinity.client.activity.userActivity.class);
                intent.putExtra(messageIds.pagePath_MESSAGE, page.getMessageUserLink());
                v.getContext().startActivity(intent);
            }
        });

        sentBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), open.furaffinity.client.activity.userActivity.class);
                intent.putExtra(messageIds.pagePath_MESSAGE, page.getMessageUserLink());
                v.getContext().startActivity(intent);
            }
        });
    }

    private void setupViewPager() {
        msgPmsMessageActivitySectionsPagerAdapter sectionsPagerAdapter = new msgPmsMessageActivitySectionsPagerAdapter(this, getSupportFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgpmsmessage);
        getElements();
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
    }
}
