package android.application.meta;

import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityItem {
    private String id;
    private String name;

    ActivityItem(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    String getAverageTime(){
        Cursor cursor = HomeActivity.db.query("ITEM",
                new String[]{DatabaseHelper.ITEM_TABLE[0],
                        DatabaseHelper.ITEM_TABLE[2],
                        DatabaseHelper.ITEM_TABLE[3]},
                DatabaseHelper.ITEM_TABLE[1] + " = ? AND " +
                        DatabaseHelper.ITEM_TABLE[3] + " IS NOT NULL",
                new String[]{id},null,null,null);

        ArrayList<DateTimeItem> dateTimeItems = new ArrayList<>();

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                dateTimeItems.add(new DateTimeItem(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)));
                cursor.moveToNext();
            }
        }
        cursor.close();

        if (dateTimeItems.size() == 0) return null;

        long averageTime = 0;
        try {
            for (DateTimeItem item : dateTimeItems){
                averageTime += item.getRange();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        averageTime /= dateTimeItems.size();

        long seconds = averageTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;

        return String.format(Locale.getDefault(),"%d h %02d min",hours,minutes);
    }
}
