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

public class HomeFragment extends Fragment implements
        ActivityListAdapter.ItemClickListener,
        ActivityListAdapter.ItemLongClickListener {

    static String activityId = "-1";

    private ArrayList<String> activityNames;
    private ActivityListAdapter adapter;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Cursor cursor = HomeActivity.db.query("ACTIVITY",
                new String[]{DatabaseHelper.ACTIVITY_TABLE[2]},
                DatabaseHelper.ACTIVITY_TABLE[1] + " = ?",
                new String[]{HomeActivity.id},null,null,null);

        activityNames = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            activityNames.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();

        RecyclerView recyclerView = view.findViewById(R.id.activity_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ActivityListAdapter(getContext(),activityNames);
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Cursor Cursor = HomeActivity.db.query("Activity",
                new String[]{DatabaseHelper.ACTIVITY_TABLE[0]},
                DatabaseHelper.ACTIVITY_TABLE[2] + " = ?",
                new String[]{adapter.getItem(position)},null,null,null);

        Cursor.moveToFirst();

        activityId = Cursor.getString(0);

        if (HomeActivity.viewPager.getAdapter() != null){
            HomeActivity.viewPager.getAdapter().notifyDataSetChanged();
            HomeActivity.viewPager.arrowScroll(View.FOCUS_RIGHT);
        }

        Cursor.close();
    }

    @Override
    public boolean onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_error_outline_red_24dp)
                .setTitle(R.string.delete_activity)
                .setMessage(getResources().getString(R.string.del_act_message) + " \"" + adapter.getItem(position) + "\"?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper.deleteActivity(HomeActivity.db,HomeActivity.id,adapter.getItem(position));
                        activityNames.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (String.valueOf(position).equals(activityId))
                            activityId = "-1";

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
}
