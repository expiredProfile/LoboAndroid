package com.metropolia.kim.xmlparser;

import android.util.Xml;

import com.metropolia.kim.loboandroiddata.Conversation;
import com.metropolia.kim.loboandroiddata.Message;
import com.metropolia.kim.loboandroiddata.Worker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimmo on 27/04/2016.
 */
public class ConversationXmlParser {
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            return readFeed(parser);
        } finally {
            //in.close();
        }
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "conversations");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("conversation")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Conversation readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "conversation");
        String topic = null;
        int id = 0;
        Message tempMessage = null;
        Worker tempWorker = null;
        List<Message> messageList = new ArrayList<>();
        List<Worker> workerList = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("topic")) {
                topic = readTopic(parser);
            } else if (xmlName.equals("ID")) {
                id = Integer.valueOf(readId(parser));
            } else if (xmlName.equals("messages")) {
                tempMessage = readMessageList(parser);
                messageList.add(tempMessage);
            } else if (xmlName.equals("memberList")) {
                tempWorker = readWorkerList(parser);
                workerList.add(tempWorker);
            } else {
                skip(parser);
            }
        }
        return new Conversation(id, topic, messageList, workerList);
    }

    private String readTopic(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "topic");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "topic");
        return title;
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ID");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ID");
        return summary;
    }

    private Message readMessageList(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "messages");
        MessageXmlParser messageXmlParser = new MessageXmlParser();
        String content = null;
        int convID = 0;
        String postName = null;
        String shortTime = null;
        String currentTime = null;
        int messageid = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("content")) {
                content = messageXmlParser.readContent(parser);
            } else if (xmlName.equals("conversationID")) {
                convID = Integer.parseInt(messageXmlParser.readId(parser));
            } else if (xmlName.equals("postName")) {
                postName = messageXmlParser.readPostName(parser);
            } else if (xmlName.equals("shortTime")) {
                shortTime = messageXmlParser.readShortTime(parser);
            } else if (xmlName.equals("currentTime")) {
                shortTime = messageXmlParser.readCurrentTime(parser);
            } else if (xmlName.equals("messageID")) {
                messageid = Integer.parseInt(messageXmlParser.readMessageid(parser));
            } else {
                skip(parser);
            }
        }
        return new Message(content, postName, convID, currentTime, shortTime, messageid);
    }

    private Worker readWorkerList(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "memberList");
        WorkerXmlParser workerXmlParser = new WorkerXmlParser();
        int id = 0;
        String name = null;
        String title = null;
        int groupID = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("groupID")) {
                groupID = Integer.parseInt(workerXmlParser.readGroupId(parser));
            } else if (xmlName.equals("name")) {
                name = workerXmlParser.readName(parser);
            } else if (xmlName.equals("title")) {
                title = workerXmlParser.readTitle(parser);
            } else if (xmlName.equals("id")) {
                id = Integer.parseInt(workerXmlParser.readId(parser));
            } else {
                skip(parser);
            }
        }
        return new Worker(id, name, title, groupID);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
