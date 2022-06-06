package open.furaffinity.client.dialogs;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;
import open.furaffinity.client.utilities.KvPair;
import open.furaffinity.client.utilities.UiControls;

public class SpinnerDialog extends DialogFragment {
    private Spinner spinner;

    private String text;
    private HashMap<String, String> data;
    private DialogListener listener;

    public void setListener(DialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.get_selection_from_user, null);

        final TextView dialogText = rootView.findViewById(R.id.dialogText);
        spinner = rootView.findViewById(R.id.spinner);

        dialogText.setText(text);
        UiControls.spinnerSetAdapter(getContext(), spinner, data,
            "", true, false);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> listener.onDialogPositiveClick(SpinnerDialog.this));
        builder.setNegativeButton(R.string.cancelButton,
            (dialog, which) -> listener.onDialogNegativeClick(SpinnerDialog.this));

        return builder.create();
    }

    public void setTitleText(String text) {
        this.text = text;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public String getSpinnerSelection() {
        return ((KvPair) spinner.getSelectedItem()).getKey();
    }

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
