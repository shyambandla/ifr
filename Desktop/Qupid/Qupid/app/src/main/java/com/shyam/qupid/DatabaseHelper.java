package com.shyam.qupid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Qupid.db";
    public static final String TABLE_NAME = "Chat_table";
    public static final String COL_1 = "UID";
    public static  final String COL_2 = "NAME";
    public static final String COL_3 = "LAST_MESSAGE";
    public static  final String COL_4 = "IMAGE_PATH";
    public static final String COL_5 = "STATUS";



    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        SQLiteDatabase db=getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL("create TABLE chat_table(UID TEXT PRIMARY KEY,NAME TEXT,LAST_MESSAGE TEXT,IMAGE_PATH TEXT,STATUS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
         onCreate(sqLiteDatabase);
    }
}
