package android.application.project;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        try {
            db = databaseHelper.getReadableDatabase();
        }
        catch (SQLiteException e){
            Toast.makeText(this,"SQLiteException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstText = firstName.getText().toString();
                String lastText = lastName.getText().toString();
                String userText = username.getText().toString();
                String passText = password.getText().toString();
                String cPassText = confirmPassword.getText().toString();

                if (firstText.isEmpty()){
                    firstName.setError("Empty Field");
                }
                if (lastText.isEmpty()){
                    lastName.setError("Empty Field");
                }
                if (userText.isEmpty()){
                    username.setError("Empty Field");
                }
                if (passText.isEmpty()){
                    password.setError("Empty Field");
                }

                if (!(cPassText.equals(passText))){
                    confirmPassword.setError("Password doesn't match");
                    return;
                }

                if (firstText.isEmpty() || lastText.isEmpty() || userText.isEmpty() ||
                    passText.isEmpty()){
                    return;
                }

                cursor = db.query("ACCOUNT", new String[]{DatabaseHelper.USERNAME},
                        DatabaseHelper.USERNAME + " = ?", new String[]{userText},
                        null,null,null);

                if (cursor.moveToFirst()){
                    username.setError("Username already taken");
                }
                else {
                    DatabaseHelper.insertAccount(db,username.getText().toString(),
                            password.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString());
                    finish();
                }
            }
        });
    }
}
