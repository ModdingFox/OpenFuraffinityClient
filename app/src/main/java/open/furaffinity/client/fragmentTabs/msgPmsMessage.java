package open.furaffinity.client.fragmentTabs;

import static open.furaffinity.client.utilities.sendPm.sendPM;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.activity.mainActivity;
import open.furaffinity.client.adapter.msgPmsMessageSectionsPagerAdapter;
import open.furaffinity.client.utilities.fabCircular;

public class msgPmsMessage extends appFragment {
    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout;

    private TextView subject;
    private ImageView userIcon;
    private TextView sentBy;
    //private TextView sentTo;
    private TextView sentDate;
    private ViewPager viewPager;
    private TabLayout tabs;

    @SuppressWarnings("FieldCanBeLocal") private fabCircular fab;
    private FloatingActionButton sendNote;

    private open.furaffinity.client.pages.msgPmsMessage page;
    private boolean isLoading = false;
    private final abstractPage.pageListener pageListener = new abstractPage.pageListener() {
        @Override public void requestSucceeded(abstractPage abstractPage) {
            subject.setText(
                ((open.furaffinity.client.pages.msgPmsMessage) abstractPage).getMessageSubject());
            Glide.with(msgPmsMessage.this).load(
                    ((open.furaffinity.client.pages.msgPmsMessage) abstractPage).getMessageUserIcon())
                .diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.loading)
                .into(userIcon);
            sentBy.setText(
                ((open.furaffinity.client.pages.msgPmsMessage) abstractPage).getMessageSentBy());
            //sentTo.setText(((open.furaffinity.client.pages.msgPmsMessage)abstractPage)
            // .getMessageSentTo());
            sentDate.setText(
                ((open.furaffinity.client.pages.msgPmsMessage) abstractPage).getMessageSentDate());

            setupViewPager((open.furaffinity.client.pages.msgPmsMessage) abstractPage);

            saveHistory();

            isLoading = false;
        }

        @Override public void requestFailed(abstractPage abstractPage) {
            isLoading = false;
            Toast.makeText(getActivity(), "Failed to load data for message", Toast.LENGTH_SHORT)
                .show();
        }
    };

    private void saveHistory() {
        ((mainActivity) requireActivity()).drawerFragmentPush(this.getClass().getName(),
            page.getPagePath());
    }

    @Override protected int getLayout() {
        return R.layout.fragment_msgpmsmessage;
    }

    protected void getElements(View rootView) {
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        subject = rootView.findViewById(R.id.subject);
        userIcon = rootView.findViewById(R.id.userIcon);
        sentBy = rootView.findViewById(R.id.sentBy);
        //sentTo = rootView.findViewById(R.id.sentTo);
        sentDate = rootView.findViewById(R.id.sentDate);
        viewPager = rootView.findViewById(R.id.view_pager);
        tabs = rootView.findViewById(R.id.tabs);

        fab = rootView.findViewById(R.id.fab);
        sendNote = new FloatingActionButton(requireContext());

        sendNote.setImageResource(R.drawable.ic_menu_newmessage);

        //noinspection deprecation
        sendNote.setBackgroundTintList(ColorStateList.valueOf(
            getResources().getColor(androidx.cardview.R.color.cardview_dark_background)));

        coordinatorLayout.addView(sendNote);

        fab.addButton(sendNote, 1.5f, 270);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;

            page = new open.furaffinity.client.pages.msgPmsMessage(page);
            page.execute();
        }
    }

    @Override protected void updateUIElements() {

    }

    private void setupViewPager(open.furaffinity.client.pages.msgPmsMessage page) {
        msgPmsMessageSectionsPagerAdapter sectionsPagerAdapter =
            new msgPmsMessageSectionsPagerAdapter(this.getActivity(), getChildFragmentManager(),
                page);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

    protected void initPages() {
        page = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), pageListener,
            ((mainActivity) requireActivity()).getMsgPmsPath());
    }

    @Override protected void updateUIElementListeners(View rootView) {
        userIcon.setOnClickListener(
            v -> ((mainActivity) requireActivity()).setUserPath(page.getMessageUserLink()));

        sentBy.setOnClickListener(
            v -> ((mainActivity) requireActivity()).setUserPath(page.getMessageUserLink()));

        sendNote.setOnClickListener(
            v -> sendPM(requireActivity(), getChildFragmentManager(), page.getMessageUser()));
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("messagePath", page.getPagePath());
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("messagePath")) {
            page = new open.furaffinity.client.pages.msgPmsMessage(getActivity(), pageListener,
                savedInstanceState.getString("messagePath"));
        }
    }
}
