package open.furaffinity.client.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.pages.controlsUserSettings;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class manageUserSettings extends Fragment {
    private static String TAG = manageUserSettings.class.getName();

    private RadioButton accept_trades_yes;
    private RadioButton accept_trades_no;
    private RadioButton accept_commissions_yes;
    private RadioButton accept_commissions_no;
    private Spinner featured_journal_id;

    private fabCircular fab;

    private open.furaffinity.client.utilities.webClient webClient;
    private controlsUserSettings page;

    private boolean isLoading = false;

    private void getElements(View rootView) {
        accept_trades_yes = rootView.findViewById(R.id.accept_trades_yes);
        accept_trades_no = rootView.findViewById(R.id.accept_trades_no);
        accept_commissions_yes = rootView.findViewById(R.id.accept_commissions_yes);
        accept_commissions_no = rootView.findViewById(R.id.accept_commissions_no);
        featured_journal_id = rootView.findViewById(R.id.featured_journal_id);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
        fab.setVisibility(View.GONE);
    }

    private void fetchPageData() {
        if(!isLoading) {
            isLoading = true;
            page = new controlsUserSettings(page);
            page.execute();
        }
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        page = new controlsUserSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((controlsUserSettings)abstractPage).getAcceptTrades()) {
                    accept_trades_yes.setChecked(true);
                } else {
                    accept_trades_no.setChecked(true);
                }

                if (((controlsUserSettings)abstractPage).getAcceptCommissions()) {
                    accept_commissions_yes.setChecked(true);
                } else {
                    accept_commissions_no.setChecked(true);
                }

                uiControls.spinnerSetAdapter(requireContext(), featured_journal_id, ((controlsUserSettings)abstractPage).getFeaturedJournalId(), ((controlsUserSettings)abstractPage).getFeaturedJournalIdCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for user settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("do", "update");
                params.put("key", page.getKey());
                params.put("accept_trades", ((accept_trades_yes.isChecked()) ? ("1") : ("0")));
                params.put("accept_commissions", ((accept_commissions_yes.isChecked()) ? ("1") : ("0")));
                params.put("featured_journal_id", ((kvPair) featured_journal_id.getSelectedItem()).getKey());
                params.put("save_settings", "Save Settings");

                try {
                    new AsyncTask<webClient, Void, Void>() {
                        @Override
                        protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                            webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsUserSettings.getPagePath(), params);
                            return null;
                        }
                    }.execute(webClient).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Could not update user settings: ", e);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_user_settings, container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
