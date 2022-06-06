package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;

public class ConfirmDialog extends DialogFragment {
    private String text;
    private DialogListener listener;

    public void setListener(DialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.dialog_confirm, null);

        final TextView dialogText = rootView.findViewById(R.id.dialogText);
        dialogText.setText(text);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> listener.onDialogPositiveClick(ConfirmDialog.this));
        builder.setNegativeButton(R.string.cancelButton,
            (dialog, which) -> listener.onDialogNegativeClick(ConfirmDialog.this));

        return builder.create();
    }

    public void setTitleText(String text) {
        this.text = text;
    }

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
