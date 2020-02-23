package android.application.meta;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";
    private static final int VERSION = 1;

    static final String[] ACCOUNT_TABLE = {"_id","USERNAME","PASSWORD","FIRSTNAME","LASTNAME"};

    DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ACCOUNT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USERNAME TEXT NOT NULL," +
                "PASSWORD TEXT NOT NULL," +
                "FIRSTNAME TEXT," +
                "LASTNAME TEXT);");
        insertAccount(db,"r","e","Rajan","Elio");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

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
        db.delete("ACCOUNT",DatabaseHelper.ACCOUNT_TABLE[1] + " = ?",
                new String[]{username});
    }
}
