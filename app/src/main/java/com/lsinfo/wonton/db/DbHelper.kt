package com.lsinfo.wonton.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by G on 2018-03-16.
 */
class DbHelper constructor(val context: Context): SQLiteOpenHelper(context, "wonton.db",  null, 17) {
    companion object {
        private var instance: DbHelper? = null

        @Synchronized
        private fun getInstance(c: Context): DbHelper {
            if (instance == null) instance = DbHelper(c)
            return instance!!
        }

        fun getReadableDatabase(context: Context): SQLiteDatabase {
            return getInstance(context).readableDatabase
        }

        fun getWritableDatabase(context: Context): SQLiteDatabase {
            return getInstance(context).writableDatabase
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        //资产表
        db.execSQL("CREATE TABLE IF NOT EXISTS assets(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "barcode TEXT, " +
                "assets_id TEXT, " +
                "name TEXT," +
                "model TEXT," +
                "deptId TEXT," +
                "dept TEXT," +
                "user TEXT," +
                "place TEXT," +
                "company TEXT," +
                "status INT)" )

        //盘点资产表
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory_assets(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pk_id TEXT unique," +
                "plan_id TEXT," +
                "assets_id TEXT, " +
                "name TEXT," +
                "model TEXT," +
                "deptId TEXT," +
                "dept TEXT," +
                "user TEXT," +
                "place TEXT," +
                "company TEXT," +
                "create_time TEXT," +
                "create_user TEXT," +
                "memo TEXT," +
                "isLocal INT," +
                "status INT)" )

        //盘点资产异常情况表
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory_error(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "inventory_assets_id TEXT, " +
                "type_id TEXT, " +
                "correct_info TEXT)")

        //资产异常类型表
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory_error_type(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT unique, " +
                "name TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        dropAllTables(db)
        onCreate(db)
    }

    private fun dropAllTables(db: SQLiteDatabase) {
        db.execSQL("drop table if exists inventory_assets")
        db.execSQL("drop table if exists assets")
        db.execSQL("drop table if exists inventory_error")
        db.execSQL("drop table if exists inventory_error_type")

        onCreate(db)
    }
}