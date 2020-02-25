package android.application.meta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ActivityDialogFragment extends AppCompatDialogFragment {
    private TextInputLayout activityName;
    private TextInputEditText activityText;
    private EditTextListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activity,null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setView(view)
                .setTitle(R.string.create_activity)
                .setPositiveButton(R.string.add,null)
                .setNegativeButton(R.string.cancel,null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = activityText.getText().toString();
                        if (name.isEmpty()){
                            activityName.setError(getResources().getString(R.string.activity_error));
                            return;
                        }

                        listener.onAdd(name);
                        dialog.dismiss();
                    }
                });
            }
        });

        activityName = view.findViewById(R.id.activity_layout);
        activityText = view.findViewById(R.id.activity_text);
        activityName.setErrorEnabled(true);

        activityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!activityText.getText().toString().isEmpty()){
                    activityName.setError(null);
                }

                activityText.clearFocus();
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return false;
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditTextListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement EditTextListener");
        }
    }

    public interface EditTextListener {
        void onAdd(String name);
    }
}
