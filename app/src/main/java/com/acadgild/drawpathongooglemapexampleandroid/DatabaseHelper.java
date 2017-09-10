package com.acadgild.drawpathongooglemapexampleandroid;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "LOCATION_DB";

    // Table columns
    public static final String _ID = "_id";
    public static final String LON_LAT = "lonlat";
    public static final String ROUTE_NAME = "route_name";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String DISTANCE = "distance";


    // Database Information
    static final String DB_NAME = "TO_DO.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LON_LAT + " TEXT NOT NULL , " + ROUTE_NAME + " TEXT, " + DATE + " TEXT, " + TIME + " TEXT , " + DISTANCE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
