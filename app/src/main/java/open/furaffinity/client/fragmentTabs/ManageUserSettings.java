package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.pages.ControlsUserSettings;
import open.furaffinity.client.submitPages.SubmitControlsUserSettings;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class ManageUserSettings extends AbstractAppFragment {
    private RadioButton accept_trades_yes;
    private RadioButton accept_trades_no;
    private RadioButton accept_commissions_yes;
    private RadioButton accept_commissions_no;
    private Spinner featured_journal_id;

    private FabCircular fab;

    private ControlsUserSettings page;

    private boolean isLoading = false;

    @Override protected int getLayout() {
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
            page = new ControlsUserSettings(page);
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        page = new ControlsUserSettings(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                if (((ControlsUserSettings) abstractPage).getAcceptTrades()) {
                    accept_trades_yes.setChecked(true);
                }
                else {
                    accept_trades_no.setChecked(true);
                }

                if (((ControlsUserSettings) abstractPage).getAcceptCommissions()) {
                    accept_commissions_yes.setChecked(true);
                }
                else {
                    accept_commissions_no.setChecked(true);
                }

                UiControls.spinnerSetAdapter(requireContext(), featured_journal_id,
                    ((ControlsUserSettings) abstractPage).getFeaturedJournalId(),
                    ((ControlsUserSettings) abstractPage).getFeaturedJournalIdCurrent(), true,
                    false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for user settings",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        fab.setOnClickListener(
            v -> new SubmitControlsUserSettings(getActivity(),
                new AbstractPage.PageListener() {
                    @Override public void requestSucceeded(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Successfully updated user settings",
                            Toast.LENGTH_SHORT).show();
                    }

                    @Override public void requestFailed(AbstractPage abstractPage) {
                        Toast.makeText(getActivity(), "Failed to update user settings",
                            Toast.LENGTH_SHORT).show();
                    }
                }, page.getKey(), ((accept_trades_yes.isChecked()) ? ("1") : ("0")),
                ((accept_commissions_yes.isChecked()) ? ("1") : ("0")),
                ((KvPair) featured_journal_id.getSelectedItem()).getKey()).execute());
    }
}
