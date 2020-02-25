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
import android.widget.Toast;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements
        ActivityListAdapter.ItemClickListener, ActivityListAdapter.ItemLongClickListener {
    private Cursor cursor;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<String> activityNames;
    private ActivityListAdapter adapter;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cursor = HomeActivity.db.query("ACTIVITY",
                new String[]{DatabaseHelper.ACTIVITY_TABLE[2]},
                DatabaseHelper.ACTIVITY_TABLE[1] + " = ?",
                new String[]{HomeActivity.id},null,null,null);

        activityNames = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            activityNames.add(cursor.getString(0));
            cursor.moveToNext();
        }

        recyclerView = view.findViewById(R.id.activity_list);
        layoutManager = new LinearLayoutManager(getContext());
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
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(),"Click: " + adapter.getItem(position),Toast.LENGTH_SHORT).show();
        HomeActivity.viewPager.arrowScroll(View.FOCUS_RIGHT);
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
