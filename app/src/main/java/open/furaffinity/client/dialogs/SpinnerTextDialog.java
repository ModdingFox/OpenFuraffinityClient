package open.furaffinity.client.dialogs;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class SpinnerTextDialog extends DialogFragment {
    private Spinner spinner;
    private EditText editText;

    private HashMap<String, String> data;
    private String currentValue;
    private SpinnerTextDialogListener listener;

    public void setListener(SpinnerTextDialogListener spinnerTextDialogListener) {
        listener = spinnerTextDialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.get_selection_and_text_from_user, null);

        spinner = rootView.findViewById(R.id.spinner);
        editText = rootView.findViewById(R.id.editText);

        if (currentValue == null) {
            UiControls.spinnerSetAdapter(
                getContext(),
                spinner,
                data,
                "",
                true,
                false);
        }
        else {
            UiControls.spinnerSetAdapter(
                getContext(),
                spinner,
                data,
                currentValue,
                true,
                false);
        }

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            listener.onDialogPositiveClick(((KvPair) spinner.getSelectedItem()).getKey(),
                editText.getText().toString());
            dismiss();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        return builder.create();
    }

    public void setData(HashMap<String, String> data, String currentValue) {
        this.data = data;
        this.currentValue = currentValue;
    }

    public interface SpinnerTextDialogListener {
        void onDialogPositiveClick(String selectedKey, String userText);
    }
}
