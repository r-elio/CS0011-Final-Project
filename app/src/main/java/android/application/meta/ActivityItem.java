package android.application.meta;

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
}
