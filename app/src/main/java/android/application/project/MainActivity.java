package android.application.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        try {
            db = databaseHelper.getReadableDatabase();
        }
        catch (SQLiteException e){
            Toast.makeText(this,"SQLiteException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    login();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    private void login(){
        if (username.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Username is empty", Toast.LENGTH_SHORT).show();
        }
        else if (password.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Password is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            String userText = username.getText().toString();
            String passText = password.getText().toString();
            cursor = db.query("ACCOUNT",new String[]{"USERNAME","PASSWORD"},
                    "USERNAME = ? AND PASSWORD = ?",new String[]{userText,passText},
                    null,null,null);
            if (cursor.moveToFirst()){
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(),"Invalid username/password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
