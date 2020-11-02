package open.furaffinity.client.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import open.furaffinity.client.R;

public class passwordDialog extends DialogFragment {
    private String TAG = passwordDialog.class.getName();

    private TextView dialogText;
    private EditText editTextTextPassword;

    private String text = null;

    public interface dialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    dialogListener listener;

    public void setListener(dialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.confirm_password_dialog, null);

        dialogText = rootView.findViewById(R.id.dialogText);
        editTextTextPassword = rootView.findViewById(R.id.editTextTextPassword);

        dialogText.setText(text);

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogPositiveClick(passwordDialog.this);
            }
        });
        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogNegativeClick(passwordDialog.this);
            }
        });

        return builder.create();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPassword() {
        return editTextTextPassword.getText().toString();
    }
}
