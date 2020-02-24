package android.application.meta;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout confirm_pass;

    private TextInputEditText userText;
    private TextInputEditText passText;
    private TextInputEditText confirmText;
    private TextInputEditText firstText;
    private TextInputEditText lastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button register;

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        try {
            db = databaseHelper.getWritableDatabase();
        }
        catch (SQLiteException e){
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_error_outline_red_24dp)
                    .setTitle(R.string.sql_exception)
                    .setMessage(R.string.db_unavailable)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_pass = findViewById(R.id.confirm_pass);
        userText = findViewById(R.id.userText);
        passText = findViewById(R.id.passText);
        confirmText = findViewById(R.id.confirmText);
        firstText = findViewById(R.id.firstText);
        lastText = findViewById(R.id.lastText);
        register = findViewById(R.id.button);

        username.setErrorEnabled(true);
        password.setErrorEnabled(true);
        confirm_pass.setErrorEnabled(true);

        userText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().isEmpty()){
                    username.setError(null);
                }
                return false;
            }
        });

        passText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().isEmpty()){
                    password.setError(null);
                }

                return false;
            }
        });

        confirmText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().isEmpty() || passText.getText() != null &&
                        v.getText().equals(passText.getText().toString())){
                    confirm_pass.setError(null);
                }

                v.clearFocus();
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStr = "";
                String passStr = "";
                String confirmStr = "";
                String firstStr = "";
                String lastStr = "";
                boolean isNullInput = false;

                if (userText.getText() != null) userStr = userText.getText().toString();
                if (passText.getText() != null) passStr = passText.getText().toString();
                if (confirmText.getText() != null) confirmStr = confirmText.getText().toString();
                if (firstText.getText() != null) firstStr = firstText.getText().toString();
                if (lastText.getText() != null) lastStr = lastText.getText().toString();

                if (userStr.isEmpty()){
                    username.setError(getResources().getString(R.string.user_error));
                    isNullInput = true;
                }
                if (passStr.isEmpty()){
                    password.setError(getResources().getString(R.string.pass_error));
                    isNullInput = true;
                }
                if (confirmStr.isEmpty()){
                    confirm_pass.setError(getResources().getString(R.string.confirm_error));
                    isNullInput = true;
                }
                if (!confirmStr.equals(passStr)){
                    confirm_pass.setError(getResources().getString(R.string.pass_match_error));
                    isNullInput = true;
                }

                if (isNullInput){
                    return;
                }

                cursor = db.query("ACCOUNT",new String[]{DatabaseHelper.ACCOUNT_TABLE[1]},
                        DatabaseHelper.ACCOUNT_TABLE[1] + " = ?",
                        new String[]{userStr},null,null,null);

                if (cursor.moveToFirst()){
                    username.setError(getResources().getString(R.string.user_taken));
                }
                else {
                    DatabaseHelper.insertAccount(db,userStr,passStr,firstStr,lastStr);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (cursor != null){
            cursor.close();
        }
        db.close();
    }
}
