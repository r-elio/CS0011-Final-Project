package android.application.meta;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db";
    private static final int VERSION = 1;

    static final String[] ACCOUNT_TABLE = {"_id","USERNAME","PASSWORD","FIRSTNAME","LASTNAME"};
    static final String[] ACTIVITY_TABLE = {"_id","ACCOUNTID","NAME"};
    static final String[] ITEM_TABLE = {"_id","ACTIVITYID","STARTDATETIME","ENDDATETIME"};

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
                "USERNAME TEXT NOT NULL," +
                "PASSWORD TEXT NOT NULL," +
                "FIRSTNAME TEXT," +
                "LASTNAME TEXT);");
        insertAccount(db,"","");

        db.execSQL("CREATE TABLE ACTIVITY (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ACCOUNTID INTEGER NOT NULL," +
                "NAME TEXT NOT NULL," +
                "FOREIGN KEY(ACCOUNTID) REFERENCES ACCOUNT(_id) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE ITEM (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ACTIVITYID INTEGER NOT NULL," +
                "STARTDATETIME TEXT NOT NULL," +
                "ENDDATETIME TEXT," +
                "FOREIGN KEY(ACTIVITYID) REFERENCES ACTIVITY(_id) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    static void recentLogin(SQLiteDatabase db, String...values){
        ContentValues recentValues = new ContentValues();
        int i = 1;
        for (String value : values){
            recentValues.put(ACCOUNT_TABLE[i],value);
            ++i;
        }
        db.update("ACCOUNT",recentValues,
                ACCOUNT_TABLE[0] + " = ?",
                new String[]{"1"});
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

    static void updateAccount(SQLiteDatabase db, String id, String...values){
        ContentValues accountValues = new ContentValues();
        int i = 1;
        for (String value : values){
            accountValues.put(ACCOUNT_TABLE[i],value);
            ++i;
        }
        db.update("ACCOUNT",accountValues,
                ACCOUNT_TABLE[0] + " = ?", new String[]{id});
    }

    static void deleteAccount(SQLiteDatabase db, String id){
        db.delete("ACCOUNT",ACCOUNT_TABLE[0] + " = ?", new String[]{id});
    }

    static void insertActivity(SQLiteDatabase db, String id, String...values){
        ContentValues activityValues = new ContentValues();
        activityValues.put(ACTIVITY_TABLE[1],Integer.valueOf(id));
        int i = 2;
        for (String value : values){
            activityValues.put(ACTIVITY_TABLE[i],value);
            ++i;
        }
        db.insert("ACTIVITY",null,activityValues);
    }

    static void updateActivity(SQLiteDatabase db, String id, String...values){
        ContentValues activityValues = new ContentValues();
        int i = 2;
        for (String value : values){
            activityValues.put(ACTIVITY_TABLE[i],value);
            ++i;
        }
        db.update("ACTIVITY",activityValues,
                ACTIVITY_TABLE[0] + " = ?", new String[]{id});
    }

    static void deleteActivity(SQLiteDatabase db, String id){
        db.delete("ACTIVITY",ACTIVITY_TABLE[0] + " = ?", new String[]{id});
    }

    static void insertItem(SQLiteDatabase db, String id, String...values){
        ContentValues itemValues = new ContentValues();
        itemValues.put(ITEM_TABLE[1],Integer.valueOf(id));
        int i = 2;
        for (String value : values){
            itemValues.put(ITEM_TABLE[i],value);
            ++i;
        }
        db.insert("ITEM",null,itemValues);
    }

    static void updateItem(SQLiteDatabase db, String id, String...values){
        ContentValues itemValues = new ContentValues();
        int i = 2;
        for (String value : values){
            itemValues.put(ITEM_TABLE[i],value);
            ++i;
        }
        db.update("ITEM", itemValues,
                ITEM_TABLE[0] + " = ?", new String[]{id});
    }

    static void deleteItem(SQLiteDatabase db, String id){
        db.delete("ITEM", ITEM_TABLE[0] + " = ?", new String[]{id});
    }
}
