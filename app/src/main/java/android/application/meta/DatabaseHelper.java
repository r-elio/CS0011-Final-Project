package android.application.meta;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";
    private static final int VERSION = 1;

    static final String[] ACCOUNT_TABLE = {"_id","USERNAME","PASSWORD","FIRSTNAME","LASTNAME"};
    static final String[] ACTIVITY_TABLE = {"_id","USERNAME","NAME"};

    DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ACCOUNT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USERNAME TEXT UNIQUE NOT NULL," +
                "PASSWORD TEXT NOT NULL," +
                "FIRSTNAME TEXT," +
                "LASTNAME TEXT);");
        insertAccount(db,"r","e","Rajan","Elio");

        db.execSQL("CREATE TABLE ACTIVITY (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USERNAME TEXT NOT NULL," +
                "NAME TEXT NOT NULL," +
                "FOREIGN KEY(USERNAME) REFERENCES ACCOUNT(USERNAME) ON DELETE CASCADE);");
        insertActivity(db,"r","Sleep");
        insertActivity(db,"r","Awake");
        insertActivity(db,"r","Study");
        insertActivity(db,"r","Play");
        insertActivity(db,"r","Outside");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    static void insertAccount(SQLiteDatabase db, String...values){
        ContentValues accountValues = new ContentValues();
        int i = 1;
        for (String value : values){
            accountValues.put(ACCOUNT_TABLE[i],value);
            ++i;
        }
        db.insert("ACCOUNT",null,accountValues);
    }

    static void updateAccount(SQLiteDatabase db, String username, String...values){
        ContentValues accountValues = new ContentValues();
        int i = 1;
        for (String value : values){
            accountValues.put(ACCOUNT_TABLE[i],value);
            ++i;
        }
        db.update("ACCOUNT",accountValues,
                ACCOUNT_TABLE[1] + " = ?", new String[]{username});
    }

    static void deleteAccount(SQLiteDatabase db, String username){
        db.delete("ACCOUNT",ACCOUNT_TABLE[1] + " = ?",
                new String[]{username});
    }

    static void insertActivity(SQLiteDatabase db, String...values){
        ContentValues activityValues = new ContentValues();
        int i = 1;
        for (String value : values){
            activityValues.put(ACTIVITY_TABLE[i],value);
            ++i;
        }
        db.insert("ACTIVITY",null,activityValues);
    }

    static void updateActivity(SQLiteDatabase db, String username, String...values){
        ContentValues activityValues = new ContentValues();
        int i = 1;
        for (String value : values){
            activityValues.put(ACTIVITY_TABLE[i],value);
            ++i;
        }
        db.update("ACTIVITY",activityValues,
                ACTIVITY_TABLE[1] + " = ?", new String[]{username});
    }

    static void deleteActivity(SQLiteDatabase db, String username, String name){
        db.delete("ACTIVITY",ACTIVITY_TABLE[1] + " = ? AND " +
                ACTIVITY_TABLE[2] + " = ?", new String[]{username,name});
    }
}
