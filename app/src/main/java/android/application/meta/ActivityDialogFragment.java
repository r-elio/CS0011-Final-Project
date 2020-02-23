package android.application.meta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class ActivityDialogFragment extends AppCompatDialogFragment {
    private TextInputEditText activityText;
    private EditTextListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activity,null);
        builder.setView(view)
                .setTitle(R.string.create_activity)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (activityText.getText() != null){
                            String name = activityText.getText().toString();
                            listener.onAdd(name);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        activityText = view.findViewById(R.id.activity_text);

        return builder.create();

        /*
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activity,null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.create_activity)
                .setPositiveButton(R.string.add,null)
                .setNegativeButton(R.string.cancel,null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = activityText.getText().toString();

                    }
                });
            }
        });

        activityText = view.findViewById(R.id.activity_text);

        return dialog;

         */
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
