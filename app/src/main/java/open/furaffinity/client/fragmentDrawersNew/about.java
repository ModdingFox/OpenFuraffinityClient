package open.furaffinity.client.fragmentDrawersNew;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.adapter.commentListAdapter;
import open.furaffinity.client.pages.user;

public class about extends open.furaffinity.client.abstractClasses.tabFragment {
    private static final String TAG = about.class.getName();

    private LinearLayoutManager layoutManager;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<commentListAdapter.ViewHolder> mAdapter;

    private static class contributor {
        private final String userPage;
        private final String userDescription;
        private final String userDate;
        private final String userAlias;

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

    private final List<contributor> contributorsList = new ArrayList<>();
    private final List<HashMap<String, String>> mDataSet = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    protected void getElements(View rootView) {
        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    protected void initPages() {
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new commentListAdapter(mDataSet, getActivity(), false);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void fetchPageData() {
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

        for (contributor currentElement : contributorsList) {
            try {
                new user(getActivity(), new abstractPage.pageListener() {
                    @Override
                    public void requestSucceeded(abstractPage abstractPage) {
                        HashMap<String, String> newUserItem = new HashMap<>();
                        newUserItem.put("userName", ((user) abstractPage).getUserName() + ((currentElement.getUserAlias() != null) ? ("(" + currentElement.getUserAlias() + ")") : ("")));
                        newUserItem.put("userIcon", ((user) abstractPage).getUserIcon());
                        newUserItem.put("userLink", currentElement.getUserPage());
                        newUserItem.put("commentDate", currentElement.getUserDate());
                        newUserItem.put("comment", currentElement.getUserDescription());
                        mDataSet.add(newUserItem);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void requestFailed(abstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to load data for contributor", Toast.LENGTH_SHORT).show();
                    }
                }, currentElement.getUserPage()).execute().get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "onCreateView: Failed to load data for contributor", e);
            }
        }
    }

    @Override
    protected void updateUIElements() {

    }

    @Override
    protected void updateUIElementListeners(View rootView) {

    }
}
