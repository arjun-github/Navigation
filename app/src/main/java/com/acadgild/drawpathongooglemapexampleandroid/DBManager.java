package com.acadgild.drawpathongooglemapexampleandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }





    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // insert map data into db

    public int insert(String lonlat, String route_name, String date, String time, String distance) {
        int i=1;
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.LON_LAT, lonlat);
        contentValue.put(DatabaseHelper.ROUTE_NAME, route_name);
        contentValue.put(DatabaseHelper.DATE, date);
        contentValue.put(DatabaseHelper.TIME, time);
        contentValue.put(DatabaseHelper.DISTANCE, distance);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
        return  i;
    }

    //fetching complete map list from db

    public Cursor fetch_complete() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.LON_LAT,DatabaseHelper.DATE,DatabaseHelper.TIME,DatabaseHelper.DISTANCE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null,null);
        if (cursor != null) {
            cursor.moveToLast();
        }
        return cursor;
    }

    //fetching single map data based on id

    public Cursor fetch_single(int id) {
        int a=id;
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.LON_LAT,DatabaseHelper.DATE,DatabaseHelper.TIME,DatabaseHelper.DISTANCE};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, DatabaseHelper._ID + "=" + a, null, null, null,null);
        if (cursor != null) {
            cursor.moveToFirst();
       }
        return cursor;
    }

    public Cursor fetch_list() {
        String[] columns = new String[] { DatabaseHelper._ID,DatabaseHelper.DATE,DatabaseHelper.TIME};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null,null);
        if (cursor != null) {
            cursor.moveToLast();
        }
        return cursor;
    }
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_NAME, null, null);
    }


    //delete a particular map
    public void delete(String _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}
