package com.metropolia.kim.loboandroiddata;

/**
 * Created by kimmo on 28/04/2016.
 */
public class Worker {
    private int id;
    private String name;
    private String title;
    private int groupID;

    public Worker() {}

    public Worker(int id, String name, String title, int gid) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.groupID = gid;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getGroupID() {
        return groupID;
    }
}
