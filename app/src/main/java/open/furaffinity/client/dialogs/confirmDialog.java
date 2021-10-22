package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import open.furaffinity.client.R;

public class confirmDialog extends DialogFragment {
    @SuppressWarnings("FieldCanBeLocal")
    private TextView dialogText;

    private String text = null;
    private dialogListener listener;

    public void setListener(dialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_confirm, null);

        dialogText = rootView.findViewById(R.id.dialogText);
        dialogText.setText(text);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, (dialog, which) -> listener.onDialogPositiveClick(confirmDialog.this));
        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> listener.onDialogNegativeClick(confirmDialog.this));

        return builder.create();
    }

    public void setTitleText(String text) {
        this.text = text;
    }

    public interface dialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
