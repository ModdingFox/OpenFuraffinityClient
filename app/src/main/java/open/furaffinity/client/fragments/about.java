package open.furaffinity.client.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.listener.EndlessRecyclerViewScrollListener;
import open.furaffinity.client.pagesOld.user;
import open.furaffinity.client.utilities.webClient;

public class about extends Fragment {
    private static final String TAG = about.class.getName();

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    private webClient webClient;
    private user page;

    private class contributor {
        private String userPage;
        private String userDescription;
        private String userDate;

        private String userAlias;

        public contributor(String userPage, String userDescription, String userDate) {
            this.userPage = userPage;
            this.userDescription = userDescription;
            this.userDate = userDate;
        }

        public contributor(String userPage, String userDescription, String userDate, String userAlias) {
            this.userPage = userPage;
            this.userDescription = userDescription;
            this.userDate = userDate;
            this.userAlias = userAlias;
        }

        public String getUserPage() {
            return userPage;
        }

        public String getUserDescription() {
            return userDescription;
        }

        public String getUserDate() {
            return userDate;
        }

        public String getUserAlias() {
            return userAlias;
        }
    }

    private List<contributor> contributorsList = new ArrayList<>();

    private List<HashMap<String, String>> mDataSet = new ArrayList<>();

    private void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void initClientAndPage(String pagePath) {
        webClient = new webClient(this.getActivity());
        page = new open.furaffinity.client.pagesOld.user(pagePath);
    }

    private void fetchPageData(contributor contributor) {
        try {
            page.execute(webClient).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Could not load page: ", e);
        }

        HashMap<String, String> newUserItem = new HashMap<>();
        newUserItem.put("userName", page.getUserName() + ((contributor.getUserAlias() != null) ? ("(" + contributor.getUserAlias() + ")") : ("")));
        newUserItem.put("userIcon", page.getUserIcon());
        newUserItem.put("userLink", contributor.getUserPage());
        newUserItem.put("commentDate", contributor.getUserDate());
        newUserItem.put("commentDate", contributor.getUserDate());
        newUserItem.put("comment", contributor.getUserDescription());
        mDataSet.add(newUserItem);
    }

    private void updateUIElements() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), false);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateUIElementListeners(View rootView) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        contributorsList.add(new contributor("/user/moddingfox/",
                "<p>\n" +
                        "  This is an unofficial opensource client for Furaffinity. This application is provided as-is with no warranties/guarantees other than the following. This application is provided for free and you will never have to pay for it, if you have paid for this application request a refund from whoever sold it to you. No feature of this application now or in the future will ever require a payment to use or be locked behind a subscription of any kind. Advertisements(once added) will only come from Furaffinity directly and will always have the option to be disabled or enabled by the user. That being said Furaffinity has server costs and this application does not, we will respect that fact and once added they will be enabled by default.\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "  This is the first version and it is pretty rough at the moment. Most things work the way they should though there are a few bugs here and there. Much of the UI design is also pretty basic and will hopefully be made to look much prettier in later releases.\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "  The goal of the OpenFuraffinityClient project is to have a client for Furaffinity that is owned/maintained by its users. All code for the official google playstore releases will always be avaliable for viewing and modification on <a href=\"https://github.com/ModdingFox/OpenFuraffinityClient\">GitHub</a>. Users who are also developers are certainly encouraged to pull the code down build the application and tinker around with it. The github repo is setup to allow for pullrequests which will allow developers to contribute updates and changes they have made to the application back to the community. Releases will be sporadic unless a critical fix is needed. Note all pullrequests must inclued the submitting developers furaffinity user page link. All contributors to this application will be listed here.\n" +
                        "</p>",
                "Initial Project Creator(2020-09-01)",
                "Tyst Jal"));

        getElements(rootView);

        for (contributor currentElement : contributorsList) {
            initClientAndPage(currentElement.getUserPage());
            fetchPageData(currentElement);
        }

        updateUIElements();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
