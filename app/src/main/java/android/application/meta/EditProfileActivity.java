package android.application.meta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {
    Cursor cursor;

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

        Button save;

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_pass = findViewById(R.id.confirm_pass);
        userText = findViewById(R.id.userText);
        passText = findViewById(R.id.passText);
        confirmText = findViewById(R.id.confirmText);
        firstText = findViewById(R.id.firstText);
        lastText = findViewById(R.id.lastText);
        save = findViewById(R.id.button);

        firstText.setHint(R.string.firstname);
        lastText.setHint(R.string.lastname);
        save.setText(R.string.save);

        username.setErrorEnabled(true);
        password.setErrorEnabled(true);
        confirm_pass.setErrorEnabled(true);

        cursor = HomeActivity.db.query("ACCOUNT",new String[]{DatabaseHelper.ACCOUNT_TABLE[1],
                        DatabaseHelper.ACCOUNT_TABLE[2],DatabaseHelper.ACCOUNT_TABLE[3],DatabaseHelper.ACCOUNT_TABLE[4]},
                DatabaseHelper.ACCOUNT_TABLE[0] + " = ?",new String[]{HomeActivity.id},
                null,null,null);

        if (cursor.moveToFirst()){
            userText.setText(cursor.getString(0));
            passText.setText(cursor.getString(1));
            firstText.setText(cursor.getString(2));
            lastText.setText(cursor.getString(3));
        }

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

        save.setOnClickListener(new View.OnClickListener() {
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

                cursor = HomeActivity.db.query("ACCOUNT",new String[]{DatabaseHelper.ACCOUNT_TABLE[0]},
                        DatabaseHelper.ACCOUNT_TABLE[1] + " = ?",
                        new String[]{userStr},null,null,null);

                if (cursor.moveToFirst() && !HomeActivity.id.equals(cursor.getString(0))){
                    username.setError(getResources().getString(R.string.user_taken));
                    return;
                }

                DatabaseHelper.updateAccount(HomeActivity.db,HomeActivity.id,userStr,passStr,firstStr,lastStr);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
    }
}
