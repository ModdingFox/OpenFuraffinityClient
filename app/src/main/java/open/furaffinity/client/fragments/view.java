package open.furaffinity.client.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import open.furaffinity.client.R;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.viewSectionsPagerAdapter;
import open.furaffinity.client.listener.OnSwipeTouchListener;
import open.furaffinity.client.utilities.WrapContentViewPager;
import open.furaffinity.client.utilities.webClient;

public class view extends Fragment {
    private static final String TAG = view.class.getName();

    private ScrollView activityViewScrollView;
    private TextView submissionTitle;
    private ImageView submissionImage;
    private LinearLayout submissionUserLinearLayout;
    private ImageView submissionUserIcon;
    private TextView submissionUser;
    private Button submissionDownload;
    private WrapContentViewPager viewPager;
    private TabLayout tabs;

    private webClient webClient;
    private open.furaffinity.client.pages.view page;

    private void getElements(View rootView) {
        activityViewScrollView = rootView.findViewById(R.id.activityViewScrollView);
        submissionTitle = rootView.findViewById(R.id.submissionTitle);
        submissionImage = rootView.findViewById(R.id.submissionImage);
        submissionUserLinearLayout = rootView.findViewById(R.id.submissionUserLinearLayout);
        submissionUserIcon = rootView.findViewById(R.id.submissionUserIcon);
        submissionUser = rootView.findViewById(R.id.submissionUser);
        submissionDownload = rootView.findViewById(R.id.submissionDownload);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.view(pagePath);
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
        submissionTitle.setText(page.getSubmissionTitle());
        Glide.with(this).load(page.getSubmissionImgLink()).into(submissionImage);
        Glide.with(this).load(page.getSubmissionUserIcon()).into(submissionUserIcon);
        submissionUser.setText(page.getSubmissionUser());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateUIElementListeners(View rootView) {
        submissionUserLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setUserPath(page.getSubmissionUserPage());
            }
        });

        activityViewScrollView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                if (page.getNext() != null) {
                    ((mainActivity) getActivity()).setViewPath(page.getNext());
                }
            }

            public void onSwipeLeft() {
                if (page.getPrev() != null) {
                    ((mainActivity) getActivity()).setViewPath(page.getPrev());
                }
            }
        });

        submissionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(page.getDownload());

                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

                Matcher fileNameMatcher = Pattern.compile("\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)\\/([^\\/]+)$").matcher(page.getDownload());

                if (fileNameMatcher.find()) {
                    String fileName = fileNameMatcher.group(5);

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(page.getSubmissionTitle() + " by " + page.getSubmissionUser());
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    downloadManager.enqueue(request);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("File naming error. Aborting download.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

    }

    private void setupViewPager() {
        viewSectionsPagerAdapter sectionsPagerAdapter = new viewSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        getElements(rootView);
        initClientAndPage(((mainActivity) getActivity()).getViewPath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners(rootView);
        setupViewPager();
        return rootView;
    }
}