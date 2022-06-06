package open.furaffinity.client.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import open.furaffinity.client.R;

public class textDialog extends DialogFragment {
    @SuppressWarnings("FieldCanBeLocal") private TextView dialogText;
    private EditText editText;

    private String text = null;
    private boolean isPassword = false;
    private dialogListener listener;

    public void setListener(dialogListener dialogListener) {
        listener = dialogListener;
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.get_text_from_user, null);

        dialogText = rootView.findViewById(R.id.dialogText);
        editText = rootView.findViewById(R.id.editText);

        dialogText.setText(text);

        if (isPassword) {
            editText.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        builder.setView(rootView);
        builder.setPositiveButton(R.string.acceptButton,
            (dialog, which) -> listener.onDialogPositiveClick(textDialog.this));
        builder.setNegativeButton(R.string.cancelButton,
            (dialog, which) -> listener.onDialogNegativeClick(textDialog.this));

        return builder.create();
    }

    public void setTitleText(String text) {
        this.text = text;
    }

    public void setIsPassword() {
        isPassword = true;
    }

    public String getText() {
        return editText.getText().toString();
    }

    public interface dialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
