package com.metropolia.kim.loboandroiddata;

/**
 * Created by kimmo on 28/04/2016.
 */
public class Alert {
    private int id;
    private String currentTime;
    private int alertCat;
    private String alertTopic;
    private int receiverGroup;
    private String postName;
    private String postTitle;

    public Alert(int id, String time, int alertCat, String topic, int receiverGroup, String postName, String postTitle) {
        this.id = id;
        this.currentTime = time;
        this.alertCat = alertCat;
        this.alertTopic = topic;
        this.receiverGroup = receiverGroup;
        this.postName = postName;
        this.postTitle = postTitle;
    }

    public Alert(){
    }

    public void setId(int id){
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String getCurrentTime(){
        return this.currentTime;
    }

    public int getAlertCat() {
        return alertCat;
    }

    public String getAlertTopic() {
        return alertTopic;
    }

    public int getReceiverGroup(){
        return this.receiverGroup;
    }

    public String getPostName() {
        return postName;
    }

    public String getPostTitle() {
        return postTitle;
    }

}
