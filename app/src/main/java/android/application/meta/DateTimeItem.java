package android.application.meta;

class DateTimeItem {
    private String startDateTime;
    private String endDateTime;

    DateTimeItem(String startTime, String endTime){
        this.startDateTime = startTime;
        this.endDateTime = endTime;
    }

    String getStartDateTime() {
        return startDateTime;
    }

    String getEndDateTime() {
        return endDateTime;
    }
}
