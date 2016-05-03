package com.metropolia.kim.loboandroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by kimmo on 27/04/2016.
 */
public class ChatProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.provider.chatProvider";
    static final String URL = "content://" + PROVIDER_NAME;
    static final Uri CONTENT_URI = Uri.parse(URL);

    private static final int CONVERSATIONS_NAME = 1;
    private static final int CONVERSATION_ID = 2;
    private static final int MESSAGES_BY_ID = 3;
    private static final int MESSAGES_LATEST_ID = 4;
    private static final int ALL_WORKERS = 5;

    private static final int INSERT_WORKER = 6;
    private static final int INSERT_CONVERSATION = 7;
    private static final int INSERT_MESSAGE = 8;

    private static final int INSERT_ALERT = 9;
    private static final int ALERTS_HISTORY = 10;

    private static final int INSERT_MEMBER = 11;
    private static final int FLUSH_MEMBER = 12;

    private static final int FLUSH_MESSAGES = 13;


    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "conversations/name",CONVERSATIONS_NAME);
        uriMatcher.addURI(PROVIDER_NAME, "conversations/id",CONVERSATION_ID);
        uriMatcher.addURI(PROVIDER_NAME, "messages",MESSAGES_BY_ID);
        uriMatcher.addURI(PROVIDER_NAME,"messages/flush",FLUSH_MESSAGES);
        uriMatcher.addURI(PROVIDER_NAME, "messages/latest",MESSAGES_LATEST_ID);
        uriMatcher.addURI(PROVIDER_NAME, "workers",ALL_WORKERS);

        uriMatcher.addURI(PROVIDER_NAME,"workers/insert",INSERT_WORKER);
        uriMatcher.addURI(PROVIDER_NAME,"conversations/insert",INSERT_CONVERSATION);
        uriMatcher.addURI(PROVIDER_NAME,"messages/insert",INSERT_MESSAGE);

        uriMatcher.addURI(PROVIDER_NAME,"alerts/insert",INSERT_ALERT);
        uriMatcher.addURI(PROVIDER_NAME,"alerts/range/#",ALERTS_HISTORY);

        uriMatcher.addURI(PROVIDER_NAME,"/members/insert",INSERT_MEMBER);
        uriMatcher.addURI(PROVIDER_NAME,"/members/flush",FLUSH_MEMBER);
    }

    private SQLiteDatabase database;
    static final String DATABASE_NAME = "LoboAndroid";
    static final String WORKERS_TABLE_NAME = "workers";
    static final String MESSAGES_TABLE_NAME = "messages";
    static final String CONVERSATIONS_TABLE_NAME = "conversations";
    static final String ALERTS_TABLE_NAME = "alerts";
    static final String CONVERSATION_MEMBER_NAME = "members";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLES =
            "CREATE TABLE " + WORKERS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " professionid TEXT NOT NULL," +
                    " title TEXT NOT NULL,"+
                    " workerid INTEGER UNIQUE NOT NULL);";

    static final String table2 = "CREATE TABLE " + MESSAGES_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " content TEXT NOT NULL, " +
            " conversationid TEXT NOT NULL," +
            " messageid INTEGER UNIQUE NOT NULL," +
            " postname TEXT NOT NULL,"
            + "shorttimestamp TEXT);";
    static final String table3 = "CREATE TABLE " + CONVERSATIONS_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY, " +
            " topic TEXT NOT NULL);";
    static final String table4 = "CREATE TABLE " + CONVERSATION_MEMBER_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " conversationid TEXT NOT NULL,"+
            " topic TEXT NOT NULL,"+
            " lastmessage TEXT NOT NULL,"+
            " workername INTEGER NOT NULL);";
    static final String table5 = "CREATE TABLE " + ALERTS_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY, " +
            " topic TEXT NOT NULL, " +
            " currenttime TEXT NOT NULL," +
            " category INTEGER NOT NULL," +
            " postname TEXT NOT NULL," +
            " receivergroup INTEGER NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLES);
            db.execSQL(table2);
            db.execSQL(table3);
            db.execSQL(table4);
            db.execSQL(table5);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + WORKERS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ALERTS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CONVERSATIONS_TABLE_NAME);
            onCreate(db);
        }
    }// databasehelper class

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        database = dbHelper.getWritableDatabase();
        return (database == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor c = null;
        switch(match){
            case CONVERSATIONS_NAME:
                c = database.query(CONVERSATION_MEMBER_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CONVERSATION_ID:
                c = database.query("conversations", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_BY_ID:
                c = database.query("messages", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MESSAGES_LATEST_ID:
                c = database.query("messages", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ALL_WORKERS:
                c = database.query("workers", projection, selection, selectionArgs, null, null, sortOrder);
                Log.d("oma","worker query()");
                break;
            case ALERTS_HISTORY:
                c = database.query("alerts", projection, selection, selectionArgs, null, null, sortOrder);
                Log.d("oma","alert history query()");
                break;
            default:
                Log.d("oma", "Invalid uri");
                break;
        }


        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);

        switch(match){
            case INSERT_WORKER:
                database.insertWithOnConflict("workers", null, values,SQLiteDatabase.CONFLICT_ROLLBACK);
                Log.d("oma","insert worker()");
                break;
            case INSERT_CONVERSATION:
                database.insert("conversations", null, values);
                break;
            case INSERT_MESSAGE:
                database.insert("messages", null, values);
                break;
            case INSERT_ALERT:
                database.insertWithOnConflict("alerts", null, values,SQLiteDatabase.CONFLICT_ROLLBACK);
                break;
            case INSERT_MEMBER:
                database.insert("members",null,values);
                break;
            default:
                Log.d("oma","Insert default");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match){
            case FLUSH_MEMBER:
                Log.d("oma","Provider delete");
                database.delete("members",null,null);
                break;
            case FLUSH_MESSAGES:
                Log.d("oma","Messages del");
                database.delete("messages",null,null);
                break;
            default:
                Log.d("oma","Provider delete fail");
                break;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
