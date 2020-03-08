package android.application.meta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements
        ActivityDialogFragment.EditTextListener,
        ItemDialogFragment.DateTimeItemListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_ID = "id";
    public static SQLiteDatabase db;
    public static String id;
    public static ViewPager viewPager;

    FloatingActionButton fab;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        try {
            db = databaseHelper.getWritableDatabase();
        }
        catch (SQLiteException e){
            Toast.makeText(this,R.string.db_unavailable,Toast.LENGTH_LONG).show();
        }

        id = getIntent().getStringExtra(EXTRA_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionPageAdapter pagerAdapter = new SectionPageAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0){
                    Intent intent = new Intent(HomeActivity.this,EditProfileActivity.class);
                    intent.putExtra(HomeActivity.EXTRA_ID, id);
                    startActivity(intent);
                }
                else if (viewPager.getCurrentItem() == 1){
                    ActivityDialogFragment dialog = new ActivityDialogFragment();
                    dialog.show(getSupportFragmentManager(),null);
                }
                else if (viewPager.getCurrentItem() == 2){
                    showDatePickerDialog();
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        fab.setImageResource(R.drawable.ic_edit_black_24dp);
                        fab.show();
                        break;
                    case 1:
                        fab.setImageResource(R.drawable.ic_add_black_24dp);
                        fab.show();
                        break;
                    case 2:
                        if (HomeFragment.activityId.equals("-1")){
                            fab.hide();
                        }
                        else {
                            fab.setImageResource(R.drawable.ic_add_black_24dp);
                            fab.show();
                        }
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }
        });
    }

    private void showDatePickerDialog(){
        new DatePickerDialog(
                HomeActivity.this,
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
                HomeActivity.this,
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

        DatabaseHelper.insertItem(db,HomeFragment.activityId,
                DateTimeItem.inputFormat.format(calendar.getTime()));

        if (viewPager.getAdapter() != null)
            viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onAddActivity(String name) {
        DatabaseHelper.insertActivity(db,HomeActivity.id,name);
        if (viewPager.getAdapter() != null){
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onUpdateDateTimeItem(String id, String startDateTime, String endDateTime) {
        DatabaseHelper.updateItem(HomeActivity.db,
                id,startDateTime,endDateTime);

        if (HomeActivity.viewPager.getAdapter() != null)
            HomeActivity.viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed(){
        if (viewPager.getAdapter() != null &&
            viewPager.getCurrentItem() == 2 &&
            !HomeFragment.activityId.equals("-1") &&
            ActivityListAdapter.selectedPosition != -1){
            HomeFragment.activityId = "-1";
            ActivityListAdapter.selectedPosition = -1;
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(1,true);
            return;
        }

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_help_outline_blue_24dp)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveRecentLogin();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                        boolean isNotificationOn = sharedPreferences.getBoolean(getResources().getString(R.string.notif_pref),false);
                        if (isNotificationOn) notifyOngoingActivities();

                        finish();
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
    }

    private void saveRecentLogin(){
        Cursor cursor = db.query("ACCOUNT", new String[]{DatabaseHelper.ACCOUNT_TABLE[1],
                        DatabaseHelper.ACCOUNT_TABLE[2]}, DatabaseHelper.ACCOUNT_TABLE[0] + " = ?",
                new String[]{id},null,null,null);

        cursor.moveToFirst();
        DatabaseHelper.recentLogin(db,cursor.getString(0),cursor.getString(1));
        cursor.close();
    }

    private void notifyOngoingActivities(){
        Cursor cursor = db.rawQuery("SELECT NAME FROM ACTIVITY WHERE ACCOUNTID = ? AND _ID IN (" +
                "SELECT DISTINCT ACTIVITYID FROM ITEM WHERE ENDDATETIME IS NULL)", new String[]{id});

        if (cursor.moveToFirst()){
            StringBuilder text = new StringBuilder();
            while (!cursor.isAfterLast()){
                text.append(cursor.getString(0).toUpperCase()).append('\n');
                cursor.moveToNext();
            }

            Intent intent = new Intent(this, NotificationService.class);
            intent.putExtra(NotificationService.EXTRA_TEXT,text.toString());
            startService(intent);
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.help :
                intent = new Intent(HomeActivity.this,HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.settings :
                intent = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout :
                logoutConfirmation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutConfirmation(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_help_outline_blue_24dp)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper.recentLogin(db,"","");
                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
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
    }

    private class SectionPageAdapter extends FragmentPagerAdapter {

        SectionPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ProfileFragment();
                case 1:
                    return new HomeFragment();
                case 2:
                    return new ActivityFragment();
            }
            return new HomeFragment();
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return getResources().getString(R.string.profile_tab);
                case 1:
                    return getResources().getString(R.string.home_tab);
                case 2:
                    return getResources().getString(R.string.activity_tab);
            }
            return null;
        }
    }
}
