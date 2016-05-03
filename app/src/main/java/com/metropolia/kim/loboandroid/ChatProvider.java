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


    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "conversations/name/#",CONVERSATIONS_NAME);
        uriMatcher.addURI(PROVIDER_NAME, "conversations/id/#",CONVERSATION_ID);
        uriMatcher.addURI(PROVIDER_NAME, "messages/#",MESSAGES_BY_ID);
        uriMatcher.addURI(PROVIDER_NAME, "messages/latest/#",MESSAGES_LATEST_ID);
        uriMatcher.addURI(PROVIDER_NAME, "workers",ALL_WORKERS);

        uriMatcher.addURI(PROVIDER_NAME,"workers/insert",INSERT_WORKER);
        uriMatcher.addURI(PROVIDER_NAME,"conversations/insert",INSERT_CONVERSATION);
        uriMatcher.addURI(PROVIDER_NAME,"messages/insert",INSERT_MESSAGE);
    }

    private SQLiteDatabase database;
    static final String DATABASE_NAME = "LoboAndroid";
    static final String WORKERS_TABLE_NAME = "workers";
    static final String MESSAGES_TABLE_NAME = "messages";
    static final String CONVERSATIONS_TABLE_NAME = "conversations";
    static final String ALERTS_TABLE_NAME = "alerts";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLES =
            "CREATE TABLE " + WORKERS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " professionid TEXT NOT NULL," +
                    " title TEXT NOT NULL);" +
                    " title TEXT NOT NULL,"+
                    " workerid INTEGER UNIQUE NOT NULL);"

                    + " CREATE TABLE " + MESSAGES_TABLE_NAME +
                    " title TEXT NOT NULL,"+
                    " workerid INTEGER UNIQUE NOT NULL);"

                    + "CREATE TABLE " + MESSAGES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " content TEXT NOT NULL, " +
                    " conversationid TEXT NOT NULL," +
                    " postname TEXT NOT NULL,"
                    + "shorttimestamp TEXT NOT NULL);"

                    + " CREATE TABLE " + CONVERSATIONS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY, " +
                    " topic TEXT NOT NULL);"

                    + "CREATE TABLE " + CONVERSATIONS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY, " +
                    " topic TEXT NOT NULL);"

                    + "CREATE TABLE " + ALERTS_TABLE_NAME +
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
                c = database.query("conversations", projection, selection, selectionArgs, null, null, sortOrder);
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
                database.insertWithOnConflict("conversations", null, values,SQLiteDatabase.CONFLICT_ROLLBACK);
                break;
            case INSERT_MESSAGE:
                database.insertWithOnConflict("messages", null, values,SQLiteDatabase.CONFLICT_ROLLBACK);
                break;
            default:
                Log.d("oma","Insert default");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
