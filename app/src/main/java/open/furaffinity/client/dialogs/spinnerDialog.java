package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

import open.furaffinity.client.R;
import open.furaffinity.client.utilities.kvPair;

public class spinnerDialog extends DialogFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private TextView dialogText;
    private Spinner spinner;

    private String text = null;
    private HashMap<String, String> data;

    public interface dialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private dialogListener listener;

    public void setListener(dialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.get_selection_from_user, null);

        dialogText = rootView.findViewById(R.id.dialogText);
        spinner = rootView.findViewById(R.id.spinner);

        dialogText.setText(text);
        open.furaffinity.client.utilities.uiControls.spinnerSetAdapter(getContext(), spinner, data, "", true, false);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> listener.onDialogPositiveClick(spinnerDialog.this));
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> listener.onDialogNegativeClick(spinnerDialog.this));

        return builder.create();
    }

    public void setTitleText(String text) {
        this.text = text;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public String getSpinnerSelection() {
        return ((kvPair) spinner.getSelectedItem()).getKey();
    }
}
