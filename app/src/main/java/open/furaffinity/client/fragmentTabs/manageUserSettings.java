package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.abstractClasses.appFragment;
import open.furaffinity.client.pages.controlsUserSettings;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class manageUserSettings extends appFragment {
    private RadioButton accept_trades_yes;
    private RadioButton accept_trades_no;
    private RadioButton accept_commissions_yes;
    private RadioButton accept_commissions_no;
    private Spinner featured_journal_id;

    private fabCircular fab;

    private controlsUserSettings page;

    private boolean isLoading = false;

    @Override
    protected int getLayout() {
        return R.layout.fragment_manage_user_settings;
    }

    protected void getElements(View rootView) {
        accept_trades_yes = rootView.findViewById(R.id.accept_trades_yes);
        accept_trades_no = rootView.findViewById(R.id.accept_trades_no);
        accept_commissions_yes = rootView.findViewById(R.id.accept_commissions_yes);
        accept_commissions_no = rootView.findViewById(R.id.accept_commissions_no);
        featured_journal_id = rootView.findViewById(R.id.featured_journal_id);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page = new controlsUserSettings(page);
            page.execute();
        }
    }

    @Override
    protected void updateUIElements() {

    }

    protected void initPages() {
        page = new controlsUserSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                if (((controlsUserSettings) abstractPage).getAcceptTrades()) {
                    accept_trades_yes.setChecked(true);
                } else {
                    accept_trades_no.setChecked(true);
                }

                if (((controlsUserSettings) abstractPage).getAcceptCommissions()) {
                    accept_commissions_yes.setChecked(true);
                } else {
                    accept_commissions_no.setChecked(true);
                }

                uiControls.spinnerSetAdapter(requireContext(), featured_journal_id, ((controlsUserSettings) abstractPage).getFeaturedJournalId(), ((controlsUserSettings) abstractPage).getFeaturedJournalIdCurrent(), true, false);

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

    protected void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(v -> new open.furaffinity.client.submitPages.submitControlsUserSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Successfully updated user settings", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
                Toast.makeText(getActivity(), "Failed to update user settings", Toast.LENGTH_SHORT).show();
            }
        }, page.getKey(), ((accept_trades_yes.isChecked()) ? ("1") : ("0")), ((accept_commissions_yes.isChecked()) ? ("1") : ("0")), ((kvPair) featured_journal_id.getSelectedItem()).getKey()).execute());
    }
}
