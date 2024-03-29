package android.application.meta;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private TextView password;
    private ImageView visibility;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        Button delete;

        TextView username = view.findViewById(R.id.username);
        TextView firstname = view.findViewById(R.id.firstname);
        TextView lastname = view.findViewById(R.id.lastname);

        password = view.findViewById(R.id.password);
        visibility = view.findViewById(R.id.visibility);

        delete = view.findViewById(R.id.delete);

        Cursor cursor = HomeActivity.db.query("ACCOUNT",new String[]{DatabaseHelper.ACCOUNT_TABLE[1],
                        DatabaseHelper.ACCOUNT_TABLE[2],DatabaseHelper.ACCOUNT_TABLE[3],DatabaseHelper.ACCOUNT_TABLE[4]},
                DatabaseHelper.ACCOUNT_TABLE[0] + " = ?",new String[]{HomeActivity.id},
                null,null,null);

        if (cursor.moveToFirst()){
            username.setText(cursor.getString(0));
            password.setText(cursor.getString(1));
            firstname.setText(cursor.getString(2));
            lastname.setText(cursor.getString(3));
            password.setTransformationMethod(new PasswordTransformationMethod());
        }

        cursor.close();

        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visibility.getContentDescription().equals(v.getResources().getString(R.string.visible_off))){
                    visibility.setImageResource(R.drawable.ic_visibility_black_24dp);
                    password.setTransformationMethod(null);
                    visibility.setContentDescription(v.getResources().getString(R.string.visible_on));
                }
                else {
                    visibility.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    visibility.setContentDescription(v.getResources().getString(R.string.visible_off));
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(R.drawable.ic_error_outline_red_24dp)
                        .setTitle(R.string.delete_account)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper.deleteAccount(HomeActivity.db,HomeActivity.id);
                                if (getActivity()!= null){
                                    DatabaseHelper.recentLogin(HomeActivity.db,"","");
                                    Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
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
        });

        return view;
    }
}
