package android.application.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "db";
    private static final int DB_VERSION = 1;

    private static final String ID = "_id";
    static final String USERNAME = "USERNAME";
    static final String PASSWORD = "PASSWORD";
    static final String FIRSTNAME = "FIRSTNAME";
    static final String LASTNAME = "LASTNAME";

    DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        currentDatabase(db,0,DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        currentDatabase(db,oldVersion,newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        currentDatabase(db,oldVersion,newVersion);
    }

    private void currentDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("CREATE TABLE ACCOUNT (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USERNAME + " TEXT," +
                PASSWORD + " TEXT," +
                FIRSTNAME + " TEXT," +
                LASTNAME + " TEXT);");
        insertAccount(db,"test","test","test","test");
    }

    static void insertAccount(SQLiteDatabase db, String username, String password, String firstName, String lastName){
        ContentValues accountValues = new ContentValues();
        accountValues.put(USERNAME,username);
        accountValues.put(PASSWORD,password);
        accountValues.put(FIRSTNAME,firstName);
        accountValues.put(LASTNAME,lastName);
        db.insert("ACCOUNT",null,accountValues);
    }
}
