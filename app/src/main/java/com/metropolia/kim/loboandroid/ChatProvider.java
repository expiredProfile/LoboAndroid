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

import java.util.HashMap;

/**
 * Created by kimmo on 27/04/2016.
 */
public class ChatProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.provider.chatProvider";
    static final String URL = "content://" + PROVIDER_NAME;
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //uriMatcher.addURI(PROVIDER_NAME, "players", PLAYERS);
        //uriMatcher.addURI(PROVIDER_NAME, "players/#", PLAYER_ID);
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
                    " title TEXT NOT NULL);"

                    + " CREATE TABLE " + MESSAGES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " content TEXT NOT NULL, " +
                    " conversationid TEXT NOT NULL," +
                    " postname TEXT NOT NULL,"
                    + "shorttimestamp TEXT NOT NULL);"

                    + " CREATE TABLE " + CONVERSATIONS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY, " +
                    " topic TEXT NOT NULL);"

                    + " CREATE TABLE " + ALERTS_TABLE_NAME +
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
        Cursor c = database.query("workers", projection, selection, selectionArgs, null, null, sortOrder);
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
        database.insert("workers", null, values);
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
