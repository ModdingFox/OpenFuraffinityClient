package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.AbstractPage;
import open.furaffinity.client.abstractClasses.AbstractAppFragment;
import open.furaffinity.client.dialogs.TextDialog;
import open.furaffinity.client.pages.ControlsSettings;
import open.furaffinity.client.submitPages.SubmitControlsSettings;
import open.furaffinity.client.utilities.FabCircular;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class ManageAccountSettings extends AbstractAppFragment {
    private EditText fa_useremail;
    private Spinner bdaymonth;
    private Spinner bdayday;
    private Spinner bdayyear;
    private Spinner viewmature;
    private Spinner timezone;
    private Switch timezone_dst;
    private Spinner fullview;
    private Spinner style;
    private Spinner stylesheet;
    private Spinner scales_enabled;
    private EditText paypal_email;
    private Spinner display_mode;
    private Spinner scales_message_enabled;
    private EditText scales_name;
    private EditText scales_plural_name;
    private EditText scales_cost;
    private Spinner account_disabled;
    private EditText newpassword;
    private EditText newpassword2;

    private FabCircular fab;

    private ControlsSettings page;

    private boolean isLoading = false;

    @Override protected int getLayout() {
        return R.layout.fragment_manage_account_settings;
    }

    protected void getElements(View rootView) {
        fa_useremail = rootView.findViewById(R.id.fa_useremail);
        bdaymonth = rootView.findViewById(R.id.bdaymonth);
        bdayday = rootView.findViewById(R.id.bdayday);
        bdayyear = rootView.findViewById(R.id.bdayyear);
        viewmature = rootView.findViewById(R.id.viewmature);
        timezone = rootView.findViewById(R.id.timezone);
        timezone_dst = rootView.findViewById(R.id.timezone_dst);
        fullview = rootView.findViewById(R.id.fullview);
        style = rootView.findViewById(R.id.style);
        stylesheet = rootView.findViewById(R.id.stylesheet);
        scales_enabled = rootView.findViewById(R.id.scales_enabled);
        paypal_email = rootView.findViewById(R.id.paypal_email);
        display_mode = rootView.findViewById(R.id.display_mode);
        scales_message_enabled = rootView.findViewById(R.id.scales_message_enabled);
        scales_name = rootView.findViewById(R.id.scales_name);
        scales_plural_name = rootView.findViewById(R.id.scales_plural_name);
        scales_cost = rootView.findViewById(R.id.scales_cost);
        account_disabled = rootView.findViewById(R.id.account_disabled);
        newpassword = rootView.findViewById(R.id.newpassword);
        newpassword2 = rootView.findViewById(R.id.newpassword2);

        fab = rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_menu_save);
        fab.setVisibility(View.GONE);
    }

    protected void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page.execute();
        }
    }

    @Override protected void updateUiElements() {

    }

    protected void initPages() {
        page = new ControlsSettings(getActivity(), new AbstractPage.PageListener() {
            @Override public void requestSucceeded(AbstractPage abstractPage) {
                fa_useremail.setText(((ControlsSettings) abstractPage).getFaUserEmail());

                UiControls.spinnerSetAdapter(requireContext(), bdaymonth,
                    ((ControlsSettings) abstractPage).getBDayMonth(),
                    ((ControlsSettings) abstractPage).getBDayMonthCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), bdayday,
                    ((ControlsSettings) abstractPage).getBDayDay(),
                    ((ControlsSettings) abstractPage).getBDayDayCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), bdayyear,
                    ((ControlsSettings) abstractPage).getBDayYear(),
                    ((ControlsSettings) abstractPage).getBDayYearCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), viewmature,
                    ((ControlsSettings) abstractPage).getViewMature(),
                    ((ControlsSettings) abstractPage).getViewMatureCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), timezone,
                    ((ControlsSettings) abstractPage).getTimezone(),
                    ((ControlsSettings) abstractPage).getTimezoneCurrent(), true, false);

                timezone_dst.setChecked(((ControlsSettings) abstractPage).getTimezoneDST());

                UiControls.spinnerSetAdapter(requireContext(), fullview,
                    ((ControlsSettings) abstractPage).getFullView(),
                    ((ControlsSettings) abstractPage).getFullViewCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), style,
                    ((ControlsSettings) abstractPage).getStyle(),
                    ((ControlsSettings) abstractPage).getStyleCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), stylesheet,
                    ((ControlsSettings) abstractPage).getStylesheet(),
                    ((ControlsSettings) abstractPage).getStylesheetCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), scales_enabled,
                    ((ControlsSettings) abstractPage).getScalesEnabled(),
                    ((ControlsSettings) abstractPage).getScalesEnabledCurrent(), true, false);

                paypal_email.setText(((ControlsSettings) abstractPage).getPayPalEmail());

                UiControls.spinnerSetAdapter(requireContext(), display_mode,
                    ((ControlsSettings) abstractPage).getDisplayMode(),
                    ((ControlsSettings) abstractPage).getDisplayModeCurrent(), true, false);
                UiControls.spinnerSetAdapter(requireContext(), scales_message_enabled,
                    ((ControlsSettings) abstractPage).getScalesMessageEnabled(),
                    ((ControlsSettings) abstractPage).getScalesMessageEnabledCurrent(), true,
                    false);

                scales_name.setText(((ControlsSettings) abstractPage).getScalesName());
                scales_plural_name.setText(((ControlsSettings) abstractPage).getScalesPluralName());
                scales_cost.setText(((ControlsSettings) abstractPage).getScalesCost());

                UiControls.spinnerSetAdapter(requireContext(), account_disabled,
                    ((ControlsSettings) abstractPage).getAccountDisabled(),
                    ((ControlsSettings) abstractPage).getAccountDisabledCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override public void requestFailed(AbstractPage abstractPage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for account settings",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUiElementListeners(View rootView) {
        fab.setOnClickListener(v -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setTitleText("Enter current password:");
            textDialog.setIsPassword();
            textDialog.setListener(new TextDialog.DialogListener() {
                @Override public void onDialogPositiveClick(DialogFragment dialog) {
                    new SubmitControlsSettings(getActivity(),
                        new AbstractPage.PageListener() {
                            @Override public void requestSucceeded(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Account settings updated",
                                    Toast.LENGTH_SHORT).show();
                            }

                            @Override public void requestFailed(AbstractPage abstractPage) {
                                Toast.makeText(getActivity(), "Failed to update account settings:" +
                                        ((SubmitControlsSettings) abstractPage).getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                            }
                        }, fa_useremail.getText().toString(),
                        ((KvPair) bdaymonth.getSelectedItem()).getKey(),
                        ((KvPair) bdayday.getSelectedItem()).getKey(),
                        ((KvPair) bdayyear.getSelectedItem()).getKey(),
                        ((KvPair) viewmature.getSelectedItem()).getKey(),
                        ((KvPair) timezone.getSelectedItem()).getKey(),
                        ((timezone_dst.isChecked()) ? ("1") : ("0")),
                        ((KvPair) fullview.getSelectedItem()).getKey(),
                        ((KvPair) style.getSelectedItem()).getKey(),
                        ((KvPair) stylesheet.getSelectedItem()).getKey(),
                        ((KvPair) scales_enabled.getSelectedItem()).getKey(),
                        paypal_email.getText().toString(),
                        ((KvPair) display_mode.getSelectedItem()).getKey(),
                        ((KvPair) scales_message_enabled.getSelectedItem()).getKey(),
                        scales_name.getText().toString(), scales_plural_name.getText().toString(),
                        scales_cost.getText().toString(),
                        ((KvPair) account_disabled.getSelectedItem()).getKey(),
                        newpassword.getText().toString(), newpassword2.getText().toString(),
                        ((TextDialog) dialog).getText()).execute();
                }

                @Override public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.show(getChildFragmentManager(), "accountPasswordDialog");
        });
    }
}
