package open.furaffinity.client.fragmentTabsNew;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.controlsSettings;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;

public class manageAccountSettings extends open.furaffinity.client.abstractClasses.tabFragment {
    private EditText fa_useremail;
    private Spinner ssl_enable;
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
        ssl_enable = rootView.findViewById(R.id.ssl_enable);
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

    protected void initPages() {
        page = new controlsSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fa_useremail.setText(((controlsSettings) abstractPage).getFaUserEmail());

                uiControls.spinnerSetAdapter(requireContext(), ssl_enable, ((controlsSettings) abstractPage).getSslEnable(), ((controlsSettings) abstractPage).getSslEnableCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdaymonth, ((controlsSettings) abstractPage).getBDayMonth(), ((controlsSettings) abstractPage).getBDayMonthCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayday, ((controlsSettings) abstractPage).getBDayDay(), ((controlsSettings) abstractPage).getBDayDayCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayyear, ((controlsSettings) abstractPage).getBDayYear(), ((controlsSettings) abstractPage).getBDayYearCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), viewmature, ((controlsSettings) abstractPage).getViewMature(), ((controlsSettings) abstractPage).getViewMatureCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), timezone, ((controlsSettings) abstractPage).getTimezone(), ((controlsSettings) abstractPage).getTimezoneCurrent(), true, false);

                timezone_dst.setChecked(((controlsSettings) abstractPage).getTimezoneDST());

                uiControls.spinnerSetAdapter(requireContext(), fullview, ((controlsSettings) abstractPage).getFullView(), ((controlsSettings) abstractPage).getFullViewCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), style, ((controlsSettings) abstractPage).getStyle(), ((controlsSettings) abstractPage).getStyleCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), stylesheet, ((controlsSettings) abstractPage).getStylesheet(), ((controlsSettings) abstractPage).getStylesheetCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_enabled, ((controlsSettings) abstractPage).getScalesEnabled(), ((controlsSettings) abstractPage).getScalesEnabledCurrent(), true, false);

                paypal_email.setText(((controlsSettings) abstractPage).getPayPalEmail());

                uiControls.spinnerSetAdapter(requireContext(), display_mode, ((controlsSettings) abstractPage).getDisplayMode(), ((controlsSettings) abstractPage).getDisplayModeCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_message_enabled, ((controlsSettings) abstractPage).getScalesMessageEnabled(), ((controlsSettings) abstractPage).getScalesMessageEnabledCurrent(), true, false);

                scales_name.setText(((controlsSettings) abstractPage).getScalesName());
                scales_plural_name.setText(((controlsSettings) abstractPage).getScalesPluralName());
                scales_cost.setText(((controlsSettings) abstractPage).getScalesCost());

                uiControls.spinnerSetAdapter(requireContext(), account_disabled, ((controlsSettings) abstractPage).getAccountDisabled(), ((controlsSettings) abstractPage).getAccountDisabledCurrent(), true, false);

                fab.setVisibility(View.VISIBLE);
                isLoading = false;
            }

            @Override
            public void requestFailed(abstractPage abstractPage) {
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
                    new open.furaffinity.client.submitPages.submitControlsSettings(getActivity(), new abstractPage.pageListener() {
                        @Override
                        public void requestSucceeded(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Account settings updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void requestFailed(abstractPage abstractPage) {
                            Toast.makeText(getActivity(), "Failed to update account settings:" + ((open.furaffinity.client.submitPages.submitControlsSettings)abstractPage).getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                            fa_useremail.getText().toString(),
                            ((kvPair) ssl_enable.getSelectedItem()).getKey(),
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
