package com.metropolia.kim.xmlparser;

import android.util.Xml;

import com.metropolia.kim.loboandroiddata.Alert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimmo on 27/04/2016.
 */
public class AlertXmlParser {
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

        parser.require(XmlPullParser.START_TAG, ns, "alerts");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("alert")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Alert readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "alert");
        int id = 0;
        String currentTime = null;
        int alertCat = 0;
        String alertTopic = null;
        int receiverGroup = 0;
        String postName = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String xmlName = parser.getName();
            if (xmlName.equals("alertCat")) {
                alertCat = Integer.parseInt(readCategory(parser));
            } else if (xmlName.equals("ID")) {
                id = Integer.valueOf(readId(parser));
            } else if (xmlName.equals("receiverGroup")) {
                receiverGroup = Integer.valueOf(readReceiverGroup(parser));
            } else if (xmlName.equals("currentTime")) {
                currentTime = readCurrentTime(parser);
            } else if (xmlName.equals("alertTopic")) {
                alertTopic = readAlertTopic(parser);
            } else if (xmlName.equals("postName")) {
                postName = readPostName(parser);
            } else {
                skip(parser);
            }
        }
        return new Alert(id, currentTime, alertCat, alertTopic, receiverGroup, postName);
    }

    private String readPostName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "postName");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "postName");
        return title;
    }

    private String readCurrentTime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "currentTime");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "currentTime");
        return title;
    }

    private String readAlertTopic(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "alertTopic");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "alertTopic");
        return title;
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ID");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ID");
        return summary;
    }

    private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "alertCat");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "alertCat");
        return summary;
    }

    private String readReceiverGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "receiverGroup");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "receiverGroup");
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
