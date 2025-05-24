package com.example.ite303_finalproject;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DB_Note extends SQLiteOpenHelper{
    public static String DB_name = "Db_Note";
    public static int DB_version = 1;
    public static String Col_title = "Note_title";
    public static String Col_description = "Note_description";
    public static String Col_id = "id";
    public static String Col_priority = "Note_priority";
    public static String Tbl_name = "tblnote";

    public DB_Note(Context context){super(context, DB_name, null,DB_version); }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + Tbl_name + " ("
                + Col_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Col_title + " TEXT, "
                + Col_description + " TEXT, "
                + Col_priority + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Tbl_name);
        onCreate(db);
    }
}
