package com.ihandy.a2014011385.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huangruihao on 16-8-31.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * @param context  e.g. Activity
     * @param name   name of the database
     * @param factory  (optional) CursorFactory （often null）
     * @param version  version of database model
     */
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    /**
     *  called when database first created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * called when database is opened
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
