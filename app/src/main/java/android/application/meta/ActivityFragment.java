package android.application.meta;

import android.app.AlertDialog;
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
import java.util.ArrayList;

public class ActivityFragment extends Fragment implements
        ActivityListAdapter.ItemClickListener,
        ActivityListAdapter.ItemLongClickListener {

    private ArrayList<String> items;
    private ActivityListAdapter adapter;

    public ActivityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity,container,false);

        Cursor cursor = HomeActivity.db.query("ITEM",
                new String[]{DatabaseHelper.ITEM_TABLE[2]},
                DatabaseHelper.ITEM_TABLE[1] + " = ?",
                new String[]{HomeFragment.activityId},null,null,null);

        items = new ArrayList<>();

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                items.add(cursor.getString(0));
                cursor.moveToNext();
            }

        }
        cursor.close();

        RecyclerView recyclerView = view.findViewById(R.id.item_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityListAdapter(getContext(),items);
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        // Update item
    }

    @Override
    public boolean onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_error_outline_red_24dp)
                .setTitle(R.string.delete_item)
                .setMessage(getResources().getString(R.string.del_item_message)
                        + " \"" + adapter.getItem(position) + "\"?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = HomeActivity.db.query("ITEM",new String[]{DatabaseHelper.ITEM_TABLE[0]},
                                DatabaseHelper.ITEM_TABLE[1] + " = ?",new String[]{HomeFragment.activityId},
                                null,null,null);

                        cursor.moveToFirst();

                        DatabaseHelper.deleteItem(HomeActivity.db,cursor.getString(0));
                        items.remove(position);
                        adapter.notifyItemRemoved(position);

                        cursor.close();
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
}
