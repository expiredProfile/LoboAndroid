package com.metropolia.kim.loboandroiddata;

/**
 * Created by kimmo on 28/04/2016.
 */
public class Message {
    private String content;
    private String postName;
    private int conversationID;
    private String currentTime;
    private String shortTimeStamp;
    private int messageID;

    public Message() {
    }

    public Message(String content, String name, int id, String currentTime, String shortTime, int messageID) {
        this.content = content;
        this.postName = name;
        this.conversationID = id;
        this.currentTime = currentTime;
        this.shortTimeStamp = shortTime;
        this.setMessageID(messageID);
    }

    public String getContent() {
        return content;
    }

    public String getPostName() {
        return postName;
    }

    public int getConversationID() {
        return this.conversationID;
    }

    public String getCurrentTime() {
        return this.currentTime;
    }

    public String getShortTime() {
        return this.shortTimeStamp;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }
}
