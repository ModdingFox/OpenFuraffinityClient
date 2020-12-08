package open.furaffinity.client.utilities;

import android.content.Context;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

public class dynamicEditItem {
    private LinearLayout linearLayout;
    private TextView textView;
    private EditText editText;
    private Spinner spinner;
    private String name;

    public dynamicEditItem(Context context, LinearLayout linearLayout, String name, String header, String value, String placeholder) {
        initDynamicEditItem(context, linearLayout, name, header, value, placeholder, Integer.MAX_VALUE, null);
    }

    public dynamicEditItem(Context context, LinearLayout linearLayout, String name, String header, String value, String placeholder, int maxLength) {
        initDynamicEditItem(context, linearLayout, name, header, value, placeholder, maxLength, null);
    }

    public dynamicEditItem(Context context, LinearLayout linearLayout, String name, String header, String value, HashMap<String, String> options) {
        initDynamicEditItem(context, linearLayout, name, header, value, "", 0, options);
    }

    private void initDynamicEditItem(Context context, LinearLayout linearLayout, String name, String header, String value, String placeholder, int maxLength, HashMap<String, String> options) {
        this.name = name;

        this.linearLayout = linearLayout;
        this.textView = new TextView(context);
        this.textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.textView.setText(header);
        this.linearLayout.addView(this.textView);

        if (options == null) {
            this.editText = new EditText(context);
            this.editText.setText(value);
            this.editText.setHint(placeholder);

            if (maxLength > 0) {
                InputFilter[] inputFilter = new InputFilter[1];
                inputFilter[0] = new InputFilter.LengthFilter(maxLength);
                this.editText.setFilters(inputFilter);
            }

            this.linearLayout.addView(this.editText);
        } else {
            this.spinner = new Spinner(context);
            open.furaffinity.client.utilities.uiControls.spinnerSetAdapter(context, this.spinner, options, value, false, false);
            this.linearLayout.addView(this.spinner);
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        if (this.editText != null) {
            return this.editText.getText().toString();
        } else if (this.spinner != null) {
            return ((kvPair) this.spinner.getSelectedItem()).getKey();
        }

        return "";
    }

    public void removeFromView() {
        this.linearLayout.removeView(textView);

        if (editText != null) {
            this.linearLayout.removeView(this.editText);
        } else if (spinner != null) {
            this.linearLayout.removeView(this.spinner);
        }
    }
}