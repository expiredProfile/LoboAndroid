package com.metropolia.kim.xmlparser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.metropolia.kim.loboandroiddata.Message;

/**
 * Created by kimmo on 27/04/2016.
 */
public class MessageXmlParser {
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
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

        parser.require(XmlPullParser.START_TAG, ns, "messages");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("player")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Message readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "message");
        String content = null;
        String postName = null;
        int convID = 0;
        String currentTime = null;
        String shortTime = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("content")) {
                content = readContent(parser);
            } else if (xmlName.equals("postName")) {
                postName = readPostName(parser);
            } else if (xmlName.equals("conversationID")) {
                convID = Integer.valueOf(readId(parser));
            } else if (xmlName.equals("currentTime")) {
                currentTime = readCurrentTime(parser);
            } else if (xmlName.equals("shortTimeStamp")) {
                shortTime = readShortTime(parser);
            } else {
                skip(parser);
            }
        }
        return new Message(content, postName, convID, currentTime, shortTime);
    }

    public String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "content");
        return title;
    }

    public String readPostName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "postName");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "postName");
        return title;
    }

    public String readCurrentTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "currentTime");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "currentTime");
        return title;
    }

    public String readShortTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "shortTime");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "shortTime");
        return title;
    }

    public String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "conversationID");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "conversationID");
        return summary;
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
