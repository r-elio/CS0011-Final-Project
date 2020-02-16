package android.application.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle data = getIntent().getExtras();
        String firstName = String.valueOf(data.get(DatabaseHelper.FIRSTNAME));
        String lastName = String.valueOf(data.get(DatabaseHelper.LASTNAME));
        Toast.makeText(getApplicationContext(),"Welcome, " + firstName + " " + lastName,Toast.LENGTH_SHORT).show();
    }
}
