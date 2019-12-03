package com.example.imoocservicedownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by archermind on 11/29/19.
 * Wzj
 * content
 * 数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper sHelper = null; //静态的对象引用
    private static final String DB_NAME = "download.db";
    private static final int VERSION = 1;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer,url text,start integer,ends integer,finished integer)";
    private static final String SQL_DROP = "drop table if exists thread_info";

    private DBHelper( Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 获得类的对象
     * @param
     */
    public static DBHelper getInstance(Context context){
        if(sHelper == null){
            sHelper = new DBHelper(context);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE);
    }

    //升级
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP);
        sqLiteDatabase.execSQL(SQL_CREATE);
    }
}
