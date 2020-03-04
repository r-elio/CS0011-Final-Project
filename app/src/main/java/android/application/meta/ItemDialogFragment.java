package android.application.meta;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ItemDialogFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private String id;
    private Button startDateTime;
    private Button endDateTime;

    private DateTimeItemListener listener;
    private Calendar calendar = Calendar.getInstance();
    private String selection;

    ItemDialogFragment(String id){
        super();
        this.id = id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item,null);

        startDateTime = view.findViewById(R.id.start_datetime);
        endDateTime = view.findViewById(R.id.end_datetime);

        Cursor cursor = HomeActivity.db.query("ITEM",
                new String[]{DatabaseHelper.ITEM_TABLE[2],DatabaseHelper.ITEM_TABLE[3]},
                DatabaseHelper.ITEM_TABLE[0] + " = ?",
                new String[]{id},null,null,null);

        if (cursor.moveToFirst()){
            startDateTime.setText(cursor.getString(0));
            endDateTime.setText(cursor.getString(1));
        }

        cursor.close();

        startDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(getResources().getString(R.string.start_datetime));
            }
        });

        endDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(getResources().getString(R.string.end_datetime));
            }
        });

        return new AlertDialog.Builder(getContext())
                                .setView(view)
                                .setTitle(R.string.update_time)
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String startText = startDateTime.getText().toString();
                                        String endText = endDateTime.getText().toString();

                                        try {
                                            if (!DateTimeItem.isDateTimeRangeValid(startText,endText)) {
                                                Toast.makeText(getContext(), R.string.invalid_time, Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        listener.onUpdateDateTimeItem(id,startText,endText);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
    }

    private void showDatePickerDialog(String selection){
        if (getContext() == null) return;

        this.selection = selection;

        try {
            if (selection.equals(getResources().getString(R.string.start_datetime))){
                Date startDate = DateTimeItem.inputFormat.parse(startDateTime.getText().toString());
                if (startDate == null) return;
                calendar.setTime(startDate);
            }
            else {
                Date endDate = DateTimeItem.inputFormat.parse(endDateTime.getText().toString());
                if (endDate == null) return;
                calendar.setTime(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        new DatePickerDialog(
                getContext(), this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        showTimePickerDialog();
    }

    private void showTimePickerDialog(){
        new TimePickerDialog(
                getContext(), this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false)
                .show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        if (selection.equals(getResources().getString(R.string.start_datetime))){
            startDateTime.setText(DateTimeItem.inputFormat.format(calendar.getTime()));
        }
        else {
            endDateTime.setText(DateTimeItem.inputFormat.format(calendar.getTime()));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DateTimeItemListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement DateTimeItemListener");
        }
    }

    public interface DateTimeItemListener {
        void onUpdateDateTimeItem(String id, String startDateTime, String endDateTime);
    }
}
