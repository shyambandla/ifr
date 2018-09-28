package com.shyam.qupid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FriendDatabse extends SQLiteOpenHelper {

    public  String DATABASE_NAME = "Qupid.db";
    public  String TABLE_NAME;
    public  String MESSAGE_ID;
    public  String MESSAGE;
    public  String TIME;
    public  String USER_ID;
    public String USER_NAME;
    public FriendDatabse(Context context,String DATABASE_NAME, String TABLE_NAME) {
        super(context, DATABASE_NAME, null, 1);
        this.DATABASE_NAME = DATABASE_NAME;
        this.TABLE_NAME = TABLE_NAME;
        this.MESSAGE = MESSAGE;
        this.TIME = TIME;
        this.USER_ID=USER_ID;
    }


    public FriendDatabse(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        SQLiteDatabase db=getWritableDatabase();
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create TABLE" + TABLE_NAME + "(MESSAGE_ID AUTO INCREMENT," + MESSAGE +"TEXT,"+ USER_NAME +"TEXT," + TIME + "REAL" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
