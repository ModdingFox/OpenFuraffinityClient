package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.kvPair;

public class spinnerTextDialog extends DialogFragment {
    private Spinner spinner;
    private EditText editText;

    private HashMap<String, String> data;
    private String currentValue;
    private spinnerTextDialogListener listener;

    public void setListener(spinnerTextDialogListener spinnerTextDialogListener) {
        listener = spinnerTextDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.get_selection_and_text_from_user, null);

        spinner = rootView.findViewById(R.id.spinner);
        editText = rootView.findViewById(R.id.editText);

        open.furaffinity.client.utilities.uiControls.spinnerSetAdapter(getContext(), spinner, data, ((currentValue == null) ? ("") : (currentValue)), true, false);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> {
            listener.onDialogPositiveClick(((kvPair) spinner.getSelectedItem()).getKey(), editText.getText().toString());
            dismiss();
        });
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        return builder.create();
    }

    public void setData(HashMap<String, String> data, String currentValue) {
        this.data = data;
        this.currentValue = currentValue;
    }

    public interface spinnerTextDialogListener {
        void onDialogPositiveClick(String selectedKey, String userText);
    }
}
