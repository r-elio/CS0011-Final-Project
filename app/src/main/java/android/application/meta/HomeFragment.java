package android.application.meta;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HomeFragment extends Fragment {
    private Cursor cursor;
    private ListView activityList;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        activityList = view.findViewById(R.id.activity_list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cursor = HomeActivity.db.query("ACTIVITY",
                new String[]{DatabaseHelper.ACTIVITY_TABLE[0],DatabaseHelper.ACTIVITY_TABLE[2]},
                DatabaseHelper.ACTIVITY_TABLE[1] + " = ?",
                new String[]{HomeActivity.id},null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,cursor,
                new String[]{DatabaseHelper.ACTIVITY_TABLE[2]},
                new int[]{android.R.id.text1},0);

        activityList.setAdapter(listAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
    }
}
