package open.furaffinity.client.fragmentTabs;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.BasePage;
import open.furaffinity.client.abstractClasses.BaseFragment;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.controlsSettings;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class manageAccountSettings extends BaseFragment {
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

    private fabCircular fab;

    private controlsSettings page;

    private boolean isLoading = false;

    @Override
    protected int getLayout() {
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

    @Override
    protected void updateUIElements() {

    }

    protected void initPages() {
        page = new controlsSettings(getActivity(), new BasePage.pageListener() {
            @Override
            public void requestSucceeded(BasePage BasePage) {
                fa_useremail.setText(((controlsSettings) BasePage).getFaUserEmail());

                uiControls.spinnerSetAdapter(requireContext(), bdaymonth, ((controlsSettings) BasePage).getBDayMonth(), ((controlsSettings) BasePage).getBDayMonthCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayday, ((controlsSettings) BasePage).getBDayDay(), ((controlsSettings) BasePage).getBDayDayCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayyear, ((controlsSettings) BasePage).getBDayYear(), ((controlsSettings) BasePage).getBDayYearCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), viewmature, ((controlsSettings) BasePage).getViewMature(), ((controlsSettings) BasePage).getViewMatureCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), timezone, ((controlsSettings) BasePage).getTimezone(), ((controlsSettings) BasePage).getTimezoneCurrent(), true, false);

                timezone_dst.setChecked(((controlsSettings) BasePage).getTimezoneDST());

                uiControls.spinnerSetAdapter(requireContext(), fullview, ((controlsSettings) BasePage).getFullView(), ((controlsSettings) BasePage).getFullViewCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), style, ((controlsSettings) BasePage).getStyle(), ((controlsSettings) BasePage).getStyleCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), stylesheet, ((controlsSettings) BasePage).getStylesheet(), ((controlsSettings) BasePage).getStylesheetCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_enabled, ((controlsSettings) BasePage).getScalesEnabled(), ((controlsSettings) BasePage).getScalesEnabledCurrent(), true, false);

                paypal_email.setText(((controlsSettings) BasePage).getPayPalEmail());

                uiControls.spinnerSetAdapter(requireContext(), display_mode, ((controlsSettings) BasePage).getDisplayMode(), ((controlsSettings) BasePage).getDisplayModeCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_message_enabled, ((controlsSettings) BasePage).getScalesMessageEnabled(), ((controlsSettings) BasePage).getScalesMessageEnabledCurrent(), true, false);

                scales_name.setText(((controlsSettings) BasePage).getScalesName());
                scales_plural_name.setText(((controlsSettings) BasePage).getScalesPluralName());
                scales_cost.setText(((controlsSettings) BasePage).getScalesCost());

                uiControls.spinnerSetAdapter(requireContext(), account_disabled, ((controlsSettings) BasePage).getAccountDisabled(), ((controlsSettings) BasePage).getAccountDisabledCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(BasePage BasePage) {
                fab.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(getActivity(), "Failed to load data for account settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(v -> {
            textDialog textDialog = new textDialog();
            textDialog.setTitleText("Enter current password:");
            textDialog.setIsPassword();
            textDialog.setListener(new textDialog.dialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    new open.furaffinity.client.submitPages.submitControlsSettings(getActivity(), new BasePage.pageListener() {
                        @Override
                        public void requestSucceeded(BasePage BasePage) {
                            Toast.makeText(getActivity(), "Account settings updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(BasePage BasePage) {
                            Toast.makeText(getActivity(), "Failed to update account settings:" + ((open.furaffinity.client.submitPages.submitControlsSettings) BasePage).getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                            fa_useremail.getText().toString(),
                            ((kvPair) bdaymonth.getSelectedItem()).getKey(),
                            ((kvPair) bdayday.getSelectedItem()).getKey(),
                            ((kvPair) bdayyear.getSelectedItem()).getKey(),
                            ((kvPair) viewmature.getSelectedItem()).getKey(),
                            ((kvPair) timezone.getSelectedItem()).getKey(),
                            ((timezone_dst.isChecked()) ? ("1") : ("0")),
                            ((kvPair) fullview.getSelectedItem()).getKey(),
                            ((kvPair) style.getSelectedItem()).getKey(),
                            ((kvPair) stylesheet.getSelectedItem()).getKey(),
                            ((kvPair) scales_enabled.getSelectedItem()).getKey(),
                            paypal_email.getText().toString(),
                            ((kvPair) display_mode.getSelectedItem()).getKey(),
                            ((kvPair) scales_message_enabled.getSelectedItem()).getKey(),
                            scales_name.getText().toString(),
                            scales_plural_name.getText().toString(),
                            scales_cost.getText().toString(),
                            ((kvPair) account_disabled.getSelectedItem()).getKey(),
                            newpassword.getText().toString(),
                            newpassword2.getText().toString(),
                            ((textDialog) dialog).getText()
                    ).execute();
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    dialog.dismiss();
                }
            });
            textDialog.show(getChildFragmentManager(), "accountPasswordDialog");
        });
    }
}
