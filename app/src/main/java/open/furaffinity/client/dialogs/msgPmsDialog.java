package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import open.furaffinity.client.R;

public class msgPmsDialog extends DialogFragment {
    private String TAG = msgPmsDialog.class.getName();

    private String user;

    public interface msgPmsDialogListener {
        public void userMessageData(String user, String subject, String body);
    }

    private msgPmsDialogListener listener;

    public void setListener(msgPmsDialogListener msgPmsDialogListener) {
        listener = msgPmsDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_msg_pms_send, null);

        EditText userNameEditText = rootView.findViewById(R.id.userNameEditText);
        EditText subjectEditText = rootView.findViewById(R.id.subjectEditText);
        EditText messageEditText = rootView.findViewById(R.id.messageEditText);

        if(this.user != null) {
            userNameEditText.setText(this.user);
        }

        builder.setPositiveButton(R.string.sendButton, (dialog, which) -> {
            listener.userMessageData(userNameEditText.getText().toString(), subjectEditText.getText().toString(), messageEditText.getText().toString());
        });

        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> {
            dismiss();
        });

        builder.setView(rootView);

        return builder.create();
    }

    public void setUser(String user) {
        this.user = user;
    }
}
