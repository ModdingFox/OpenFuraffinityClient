package open.furaffinity.client.fragmentTabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import open.furaffinity.client.R;
import open.furaffinity.client.abstractClasses.abstractPage;
import open.furaffinity.client.dialogs.textDialog;
import open.furaffinity.client.pages.controlsSettings;
import open.furaffinity.client.utilities.fabCircular;
import open.furaffinity.client.utilities.kvPair;
import open.furaffinity.client.utilities.uiControls;
import open.furaffinity.client.utilities.webClient;

public class manageAccountSettings extends Fragment {
    private static String TAG = manageAccountSettings.class.getName();

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

    private open.furaffinity.client.utilities.webClient webClient;
    private controlsSettings page;

    private boolean isLoading = false;

    private void getElements(View rootView) {
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

    private void fetchPageData() {
        if (!isLoading) {
            isLoading = true;
            page.execute();
        }
    }

    private void initPages() {
        webClient = new webClient(requireContext());

        page = new controlsSettings(getActivity(), new abstractPage.pageListener() {
            @Override
            public void requestSucceeded(abstractPage abstractPage) {
                fa_useremail.setText(((controlsSettings)abstractPage).getFaUserEmail());

                uiControls.spinnerSetAdapter(requireContext(), ssl_enable, ((controlsSettings)abstractPage).getSslEnable(), ((controlsSettings)abstractPage).getSslEnableCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdaymonth, ((controlsSettings)abstractPage).getBDayMonth(), ((controlsSettings)abstractPage).getBDayMonthCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayday, ((controlsSettings)abstractPage).getBDayDay(), ((controlsSettings)abstractPage).getBDayDayCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), bdayyear, ((controlsSettings)abstractPage).getBDayYear(), ((controlsSettings)abstractPage).getBDayYearCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), viewmature, ((controlsSettings)abstractPage).getViewMature(), ((controlsSettings)abstractPage).getViewMatureCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), timezone, ((controlsSettings)abstractPage).getTimezone(), ((controlsSettings)abstractPage).getTimezoneCurrent(), true, false);

                timezone_dst.setChecked(((controlsSettings)abstractPage).getTimezoneDST());

                uiControls.spinnerSetAdapter(requireContext(), fullview, ((controlsSettings)abstractPage).getFullView(), ((controlsSettings)abstractPage).getFullViewCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), style, ((controlsSettings)abstractPage).getStyle(), ((controlsSettings)abstractPage).getStyleCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), stylesheet, ((controlsSettings)abstractPage).getStylesheet(), ((controlsSettings)abstractPage).getStylesheetCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_enabled, ((controlsSettings)abstractPage).getScalesEnabled(), ((controlsSettings)abstractPage).getScalesEnabledCurrent(), true, false);

                paypal_email.setText(((controlsSettings)abstractPage).getPayPalEmail());

                uiControls.spinnerSetAdapter(requireContext(), display_mode, ((controlsSettings)abstractPage).getDisplayMode(), ((controlsSettings)abstractPage).getDisplayModeCurrent(), true, false);
                uiControls.spinnerSetAdapter(requireContext(), scales_message_enabled, ((controlsSettings)abstractPage).getScalesMessageEnabled(), ((controlsSettings)abstractPage).getScalesMessageEnabledCurrent(), true, false);

                scales_name.setText(((controlsSettings)abstractPage).getScalesName());
                scales_plural_name.setText(((controlsSettings)abstractPage).getScalesPluralName());
                scales_cost.setText(((controlsSettings)abstractPage).getScalesCost());

                uiControls.spinnerSetAdapter(requireContext(), account_disabled, ((controlsSettings)abstractPage).getAccountDisabled(), ((controlsSettings)abstractPage).getAccountDisabledCurrent(), true, false);

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

    private void updateUIElementListeners(View rootView) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog textDialog = new textDialog();
                textDialog.setTitleText("Enter current password:");
                textDialog.setIsPassword();
                textDialog.setListener(new textDialog.dialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("do", "update");
                        params.put("fa_useremail", fa_useremail.getText().toString());
                        params.put("ssl_enable", ((kvPair) ssl_enable.getSelectedItem()).getKey());
                        params.put("bdaymonth", ((kvPair) bdaymonth.getSelectedItem()).getKey());
                        params.put("bdayday", ((kvPair) bdayday.getSelectedItem()).getKey());
                        params.put("bdayyear", ((kvPair) bdayyear.getSelectedItem()).getKey());
                        params.put("viewmature", ((kvPair) viewmature.getSelectedItem()).getKey());
                        params.put("timezone", ((kvPair) timezone.getSelectedItem()).getKey());
                        params.put("timezone_dst", ((timezone_dst.isChecked()) ? ("1") : ("0")));
                        params.put("fullview", ((kvPair) fullview.getSelectedItem()).getKey());
                        params.put("style", ((kvPair) style.getSelectedItem()).getKey());
                        params.put("stylesheet", ((kvPair) stylesheet.getSelectedItem()).getKey());
                        params.put("scales_enabled", ((kvPair) scales_enabled.getSelectedItem()).getKey());
                        params.put("paypal_email", paypal_email.getText().toString());
                        params.put("display_mode", ((kvPair) display_mode.getSelectedItem()).getKey());
                        params.put("scales_message_enabled", ((kvPair) scales_message_enabled.getSelectedItem()).getKey());
                        params.put("scales_name", scales_name.getText().toString());
                        params.put("scales_plural_name", scales_plural_name.getText().toString());
                        params.put("scales_cost", scales_cost.getText().toString());
                        params.put("account_disabled", ((kvPair) account_disabled.getSelectedItem()).getKey());
                        params.put("newpassword", newpassword.getText().toString());
                        params.put("newpassword2", newpassword2.getText().toString());
                        params.put("oldpassword", ((textDialog) dialog).getText());

                        try {
                            new AsyncTask<webClient, Void, Void>() {
                                @Override
                                protected Void doInBackground(open.furaffinity.client.utilities.webClient... webClients) {
                                    webClients[0].sendPostRequest(open.furaffinity.client.utilities.webClient.getBaseUrl() + controlsSettings.getPagePath(), params);
                                    return null;
                                }
                            }.execute(webClient).get();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Could not update account settings: ", e);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                textDialog.show(getChildFragmentManager(), "accountPasswordDialog");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_account_settings, container, false);
        getElements(rootView);
        initPages();
        fetchPageData();
        updateUIElementListeners(rootView);
        return rootView;
    }
}
