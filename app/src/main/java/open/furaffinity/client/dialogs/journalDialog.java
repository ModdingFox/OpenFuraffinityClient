package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;

public class journalDialog extends DialogFragment {
    private String subject;
    private String body;
    private journalDialogListener listener;

    public void setListener(journalDialogListener journalDialogListener) {
        listener = journalDialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.journal_send, null);

        EditText subjectEditText = rootView.findViewById(R.id.subjectEditText);
        EditText messageEditText = rootView.findViewById(R.id.messageEditText);
        Switch lockCommentsSwitch = rootView.findViewById(R.id.lockCommentsSwitch);
        Switch makeFeaturedSwitch = rootView.findViewById(R.id.makeFeaturedSwitch);

        if (this.subject != null) {
            subjectEditText.setText(this.subject);
        }

        if (this.body != null) {
            messageEditText.setText(this.body);
        }

        builder.setPositiveButton(R.string.sendButton, (dialog, which) -> {
            listener.onDialogPositiveClick(subjectEditText.getText().toString(),
                messageEditText.getText().toString(), lockCommentsSwitch.isChecked(),
                makeFeaturedSwitch.isChecked());
            dismiss();
        });

        builder.setNegativeButton(R.string.cancelButton, (dialog, which) -> dismiss());

        builder.setView(rootView);

        return builder.create();
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public interface journalDialogListener {
        void onDialogPositiveClick(String subject, String body, boolean lockComments,
                                   boolean makeFeatured);
    }
}
