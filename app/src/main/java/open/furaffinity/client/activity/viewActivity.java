package open.furaffinity.client.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.viewActivitySectionsPagerAdapter;
import open.furaffinity.client.listener.OnSwipeTouchListener;
import open.furaffinity.client.pages.view;
import open.furaffinity.client.utilities.WrapContentViewPager;
import open.furaffinity.client.utilities.messageIds;
import open.furaffinity.client.utilities.webClient;

public class viewActivity extends AppCompatActivity {
    private static final String TAG = viewActivity.class.getName();

    private ScrollView activityViewScrollView;
    private TextView submissionTitle;
    private ImageView submissionImage;
    private LinearLayout submissionUserLinearLayout;
    private ImageView submissionUserIcon;
    private TextView submissionUser;
    private WrapContentViewPager viewPager;

    private webClient webClient;
    private view page;

    private void getElements() {
        activityViewScrollView = findViewById(R.id.activityViewScrollView);
        submissionTitle = findViewById(R.id.submissionTitle);
        submissionImage = findViewById(R.id.submissionImage);
        submissionUserLinearLayout = findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = findViewById(R.id.submissionUserIcon);
        submissionUser = findViewById(R.id.submissionUser);
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
        page = new view(pagePath);
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
        submissionTitle.setText(page.getSubmissionTitle());
        Glide.with(this).load(page.getDownload()).into(submissionImage);
        Glide.with(this).load(page.getSubmissionUserIcon()).into(submissionUserIcon);
        submissionUser.setText(page.getSubmissionUser());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateUIElementListeners() {
        submissionUserLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), userActivity.class);
                intent.putExtra(messageIds.pagePath_MESSAGE, page.getSubmissionUserPage());
                v.getContext().startActivity(intent);
            }
        });

        activityViewScrollView.setOnTouchListener(new OnSwipeTouchListener(viewActivity.this) {
            public void onSwipeRight() {
                if (page.getNext() != null) {
                    Intent intent = new Intent(viewPager.getContext(), viewActivity.class);
                    intent.putExtra(messageIds.pagePath_MESSAGE, page.getNext());
                    viewPager.getContext().startActivity(intent);
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    Intent intent = new Intent(viewPager.getContext(), viewActivity.class);
                    intent.putExtra(messageIds.pagePath_MESSAGE, page.getPrev());
                    viewPager.getContext().startActivity(intent);
                }
            }
        });
    }

    private void setupViewPager() {
        viewActivitySectionsPagerAdapter sectionsPagerAdapter = new viewActivitySectionsPagerAdapter(this, getSupportFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getElements();
        initClientAndPage(getPagePath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
    }
}