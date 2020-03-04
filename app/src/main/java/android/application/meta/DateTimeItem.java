package android.application.meta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DateTimeItem {
    private static final String inputPattern = "yyyy-MM-dd HH:mm";
    private static final String outputPattern = "h:mm a";

    static SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
    private static SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

    private String id;
    private String startDateTime;
    private String endDateTime;

    DateTimeItem(String id, String startTime, String endTime){
        this.id = id;
        this.startDateTime = startTime;
        this.endDateTime = endTime;
    }

    String getId() { return id; }

    String getStartDateTime() {
        return startDateTime;
    }

    String getEndDateTime() {
        return endDateTime;
    }

    long getRange() throws ParseException {
        if (startDateTime == null || endDateTime == null) return 0;
        Date startDate = inputFormat.parse(startDateTime);
        Date endDate = inputFormat.parse(endDateTime);
        if (startDate == null || endDate == null) return 0;
        return  endDate.getTime() - startDate.getTime();
    }

    String getStartTime() throws ParseException {
        if (startDateTime == null) return null;
        Date date = inputFormat.parse(startDateTime);

        if (date == null) return null;
        return outputFormat.format(date);
    }

    String getEndTime() throws ParseException {
        if (endDateTime == null) return null;
        Date date = inputFormat.parse(endDateTime);

        if (date == null) return null;
        return outputFormat.format(date);
    }

    String getTimeRange() throws ParseException {
        if (startDateTime == null || endDateTime == null) return null;
        Date startDate = inputFormat.parse(startDateTime);
        Date endDate = inputFormat.parse(endDateTime);

        if (startDate == null || endDate == null) return null;
        long range = endDate.getTime() - startDate.getTime();
        long seconds = range / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;

        return String.format(Locale.getDefault(),"%d h %02d min",hours,minutes);
    }
}
