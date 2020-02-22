package android.application.meta;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    private TextInputLayout username;
    private  TextInputLayout password;
    private TextInputEditText userText;
    private TextInputEditText passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login;
        TextView register;

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(this);
        try {
            db = databaseHelper.getReadableDatabase();
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
        userText = findViewById(R.id.userText);
        passText = findViewById(R.id.passText);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        username.setErrorEnabled(true);
        password.setErrorEnabled(true);

        userText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (userText.getText() != null && !userText.getText().toString().isEmpty()){
                    username.setError(null);
                }

                return false;
            }
        });

        passText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (passText.getText() != null && !passText.getText().toString().isEmpty()){
                    password.setError(null);
                }

                passText.clearFocus();
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStr = "";
                String passStr = "";

                if (userText.getText() != null) userStr = userText.getText().toString();
                if (passText.getText() != null) passStr = passText.getText().toString();

                boolean isNullInput = false;

                if (userStr.isEmpty()){
                    username.setError(getResources().getString(R.string.user_error));
                    isNullInput = true;
                }
                if (passStr.isEmpty()){
                    password.setError(getResources().getString(R.string.pass_error));
                    isNullInput = true;
                }

                if (isNullInput){
                    return;
                }

                cursor = db.query("ACCOUNT",new String[]{DatabaseHelper.ACCOUNT_TABLE[1],DatabaseHelper.ACCOUNT_TABLE[2]},
                        DatabaseHelper.ACCOUNT_TABLE[1] + " = ? AND " + DatabaseHelper.ACCOUNT_TABLE[2] + " = ?",
                        new String[]{userStr,passStr},null,null,null);

                if (cursor.moveToFirst()){
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    intent.putExtra(HomeActivity.EXTRA_USER,userStr);
                    startActivity(intent);
                    finish();
                }
                else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.ic_error_outline_red_24dp)
                            .setTitle(R.string.login_failed)
                            .setMessage(R.string.login_failed_message)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error_outline_yellow_24dp)
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
                .setCancelable(false)
                .create()
                .show();
    }
}
