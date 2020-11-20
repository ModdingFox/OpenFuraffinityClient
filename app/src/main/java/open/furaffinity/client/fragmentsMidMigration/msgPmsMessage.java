package open.furaffinity.client.fragmentsMidMigration;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.msgPmsMessageSectionsPagerAdapter;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.webClient;

import static open.furaffinity.client.utilities.sendPm.sendPM;

public class msgPmsMessage extends Fragment {
    private static final String TAG = msgPmsMessage.class.getName();

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;

    private TextView subject;
    private ImageView userIcon;
    private TextView sentBy;
    //private TextView sentTo;
    private TextView sentDate;
    private WebView webView;
    private ViewPager viewPager;
    private TabLayout tabs;

    private fabCircular fab;
    private FloatingActionButton sendNote;

    private webClient webClient;
    private open.furaffinity.client.pages.msgPmsMessage page;

    private void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        subject = rootView.findViewById(R.id.subject);
        userIcon = rootView.findViewById(R.id.userIcon);
        sentBy = rootView.findViewById(R.id.sentBy);
        //sentTo = rootView.findViewById(R.id.sentTo);
        sentDate = rootView.findViewById(R.id.sentDate);
        webView = rootView.findViewById(R.id.webView);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);

        fab = rootView.findViewById(R.id.fab);
        sendNote = new FloatingActionButton(getContext());

        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        sendNote.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(sendNote);

        fab.addButton(sendNote, 1.5f, 270);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {

            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to load data for message", Toast.LENGTH_SHORT).show();
            }
        }, pagePath);
    }

    private void fetchPageData() {
        try {
            page.execute().get();
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
                ((mainActivity) getActivity()).setUserPath(page.getMessageUserLink());
            }
        });

        sentBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((mainActivity) getActivity()).setUserPath(page.getMessageUserLink());
            }
        });

        sendNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPM(getActivity(), getChildFragmentManager(), page.getMessageUser());
            }
        });
    }

    private void setupViewPager() {
        msgPmsMessageSectionsPagerAdapter sectionsPagerAdapter = new msgPmsMessageSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(), page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msgpmsmessage, container, false);
        getElements(rootView);
        initClientAndPage(((mainActivity) getActivity()).getMsgPmsPath());
        fetchPageData();
        checkPageLoaded();
        updateUIElements();
        updateUIElementListeners();
        setupViewPager();
        return rootView;
    }
}
