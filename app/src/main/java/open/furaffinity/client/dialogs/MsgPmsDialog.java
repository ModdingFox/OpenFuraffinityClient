package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;

public class MsgPmsDialog extends DialogFragment {
    private String user;
    private MsgPmsDialogListener listener;

    public void setListener(MsgPmsDialogListener msgPmsDialogListener) {
        listener = msgPmsDialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View rootView = requireActivity()
            .getLayoutInflater()
            .inflate(R.layout.fragment_msg_pms_send, null);

        final EditText userNameEditText = rootView.findViewById(R.id.userNameEditText);
        final EditText subjectEditText = rootView.findViewById(R.id.subjectEditText);
        final EditText messageEditText = rootView.findViewById(R.id.messageEditText);

        if (this.user != null) {
            userNameEditText.setText(this.user);
        }

        builder.setPositiveButton(R.string.sendButton,
            (dialog, which) -> {
                listener.userMessageData(userNameEditText.getText().toString(),
                    subjectEditText.getText().toString(), messageEditText.getText().toString());
            });

        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        builder.setView(rootView);

        return builder.create();
    }

    public void setUser(String user) {
        this.user = user;
    }

    public interface MsgPmsDialogListener {
        void userMessageData(String user, String subject, String body);
    }
}
