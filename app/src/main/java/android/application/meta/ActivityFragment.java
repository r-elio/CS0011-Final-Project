package android.application.meta;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityFragment extends Fragment implements
        ItemListAdapter.ItemClickListener,
        ItemListAdapter.ItemLongClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private ArrayList<DateTimeItem> dateTimeItems;
    private ItemListAdapter adapter;

    private int selectedPosition;
    private Calendar calendar = Calendar.getInstance();

    public ActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity,container,false);

        Cursor cursor = HomeActivity.db.query("ITEM",
                new String[]{
                        DatabaseHelper.ITEM_TABLE[0],
                        DatabaseHelper.ITEM_TABLE[2],
                        DatabaseHelper.ITEM_TABLE[3]},
                DatabaseHelper.ITEM_TABLE[1] + " = ?",
                new String[]{HomeFragment.activityId},null,null,
                DatabaseHelper.ITEM_TABLE[2]);

        dateTimeItems = new ArrayList<>();

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                dateTimeItems.add(new DateTimeItem(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)));
                cursor.moveToNext();
            }
        }
        cursor.close();

        RecyclerView recyclerView = view.findViewById(R.id.item_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemListAdapter(getContext(),dateTimeItems);
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (adapter.getItem(position).getEndDateTime() == null){
            showDatePickerDialog();
        }
        else {
            ItemDialogFragment dialog = new ItemDialogFragment(adapter.getItem(position).getId());
            dialog.setCancelable(false);

            if (getFragmentManager() == null) return;
            dialog.show(getFragmentManager(),null);
        }

        selectedPosition = position;
    }

    @Override
    public boolean onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_error_outline_red_24dp)
                .setTitle(R.string.delete_item)
                .setMessage(R.string.del_item_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper.deleteItem(HomeActivity.db,adapter.getItem(position).getId());
                        dateTimeItems.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (HomeActivity.viewPager.getAdapter() != null)
                            HomeActivity.viewPager.getAdapter().notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();

        return true;
    }

    private void showDatePickerDialog(){
        if (getContext() == null) return;
        new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
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
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false)
                .show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);

        try {
            if (DateTimeItem.isDateTimeRangeValid(
                    adapter.getItem(selectedPosition).getStartDateTime(),
                    DateTimeItem.inputFormat.format(calendar.getTime()))){
                DatabaseHelper.updateItem(HomeActivity.db,
                        adapter.getItem(selectedPosition).getId(),
                        adapter.getItem(selectedPosition).getStartDateTime(),
                        DateTimeItem.inputFormat.format(calendar.getTime()));
            }
            else {
                Toast.makeText(getContext(),R.string.invalid_time,Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (HomeActivity.viewPager.getAdapter() != null)
            HomeActivity.viewPager.getAdapter().notifyDataSetChanged();
    }
}
