package com.metropolia.kim.loboandroiddata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimmo on 28/04/2016.
 */
public class Conversation {
    private int id;
    private String topic;
    private List<Message> messages;
    private List<Worker> memberList;

    public Conversation() {
    }

    public Conversation(int id, String topic, List<Message> messageList, List<Worker> list) {
        this.id = id;
        this.topic = topic;
        this.messages = messageList;
        this.memberList = list;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public int getID() {
        return this.id;
    }

    public String getTopic() {
        return this.topic;
    }

    public List<Worker> getMemberList() {
        return memberList;
    }
}
