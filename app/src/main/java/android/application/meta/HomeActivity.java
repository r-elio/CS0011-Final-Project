package android.application.meta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity implements
        ActivityDialogFragment.EditTextListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_ID = "id";
    public static SQLiteDatabase db;
    public static String id;
    public static ViewPager viewPager;

    FloatingActionButton fab;

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
                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(),null);
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
                    case 1: case 2:
                        fab.setImageResource(R.drawable.ic_add_black_24dp);
                        fab.show();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }
        });
    }

    @Override
    public void onAddActivity(String name) {
        DatabaseHelper.insertActivity(db,HomeActivity.id,name);
        if (viewPager.getAdapter() != null){
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DatabaseHelper.insertItem(db,HomeFragment.activityId,hourOfDay + ":" + minute);
        if (viewPager.getAdapter() != null){
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_help_outline_blue_24dp)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
