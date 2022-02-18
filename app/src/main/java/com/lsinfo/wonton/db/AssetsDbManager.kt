package com.lsinfo.wonton.db

import android.content.ContentValues
import android.content.Context
import com.lsinfo.wonton.model.AssetsModel

/**
 * Created by G on 2018-03-19.
 */
object AssetsDbManager {
    private const val BARCODE = "barcode"
    private const val ASSETS_ID = "assets_id"
    private const val NAME = "name"
    private const val MODEL = "model"
    private const val DEPT_ID = "deptId"
    private const val DEPT = "dept"
    private const val USER = "user"
    private const val PLACE = "place"
    private const val STATUS = "status"
    private const val COMPANY = "company"

    private const val DB_TABLE = "assets"//表名

    /**
     * 新增一条数据
     */
    fun insert(context: Context, item: AssetsModel?, isTransaction: Boolean = true) : Long{
        if (null == item) {
            return -1L
        }
        val db = DbHelper.getWritableDatabase(context)
        var id: Long = -1
        if (isTransaction) db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(BARCODE, item.barcode)
            values.put(ASSETS_ID, item.assetsId)
            values.put(NAME, item.name)
            values.put(MODEL, item.model)
            values.put(DEPT, item.dept)
            values.put(DEPT_ID, item.deptId)
            values.put(USER, item.user)
            values.put(PLACE, item.place)
            values.put(STATUS, item.status)
            values.put(COMPANY, item.company)
            id = db.insert(DB_TABLE, "", values)
            // 设置事务执行的标志为成功
            if (isTransaction) db.setTransactionSuccessful()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } finally {
            if (isTransaction){
                db.endTransaction()
                db.close()
            }
        }
        return id
    }

    /**
     * 新增多条数据
     */
    fun batchInsert(context: Context, items: List<AssetsModel?>, isTransaction: Boolean = true) {
        val db = DbHelper.getWritableDatabase(context)
        if (isTransaction) db.beginTransaction()
        try {
            items.forEach {
                if (it != null){
                    val values = ContentValues()
                    values.put(BARCODE, it.barcode)
                    values.put(ASSETS_ID, it.assetsId)
                    values.put(NAME, it.name)
                    values.put(MODEL, it.model)
                    values.put(DEPT_ID, it.deptId)
                    values.put(DEPT, it.dept)
                    values.put(USER, it.user)
                    values.put(PLACE, it.place)
                    values.put(STATUS, it.status)
                    values.put(COMPANY, it.company)
                    db.insert(DB_TABLE, "", values)
                }
            }
            // 设置事务执行的标志为成功
            if (isTransaction) db.setTransactionSuccessful()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } finally {
            if (isTransaction){
                db.endTransaction()
                db.close()
            }
        }
    }

    /**
     * 更新一条数据
     */
    fun update(context: Context, item: AssetsModel, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            val values = ContentValues()
            values.put(BARCODE, item.barcode)
            values.put(ASSETS_ID, item.assetsId)
            values.put(NAME, item.name)
            values.put(MODEL, item.model)
            values.put(DEPT_ID, item.deptId)
            values.put(DEPT, item.dept)
            values.put(USER, item.user)
            values.put(PLACE, item.place)
            values.put(STATUS, item.status)
            values.put(COMPANY, item.company)
            count = db.update(DB_TABLE, values, "$ASSETS_ID = ?", arrayOf(item.assetsId))
            if (isTransaction) db.setTransactionSuccessful()
        } finally {
            if (isTransaction){
                db.endTransaction()
                db.close()
            }
        }
        return count
    }

    /**
     * 删除一条数据
     */
    fun delete(context: Context, assetsId: String, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            count = db.delete(DB_TABLE, "$ASSETS_ID = ?", arrayOf(assetsId))
            if (isTransaction) db.setTransactionSuccessful()
        } finally {
            if (isTransaction){
                db.endTransaction()
                db.close()
            }
        }
        return count
    }

    /**
     * 删除所有数据
     */
    fun deleteAll(context: Context, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            count = db.delete(DB_TABLE, "",arrayOf() )
            if (isTransaction) db.setTransactionSuccessful()
        } finally {
            if (isTransaction){
                db.endTransaction()
                db.close()
            }
        }
        return count
    }

    /**
     * 根据 Id 获取对象
     */
    fun get(context: Context, assetsId: String, autoCloseDb: Boolean = true): AssetsModel? {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$ASSETS_ID = ?",
                arrayOf(assetsId), null, null, null)
        var item: AssetsModel? = null
        if (cursor != null) {
            if (cursor.moveToFirst()){
                item = AssetsModel(
                        barcode = cursor.getString(cursor.getColumnIndex(BARCODE)),
                        assetsId = cursor.getString(cursor.getColumnIndex(ASSETS_ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME)),
                        model = cursor.getString(cursor.getColumnIndex(MODEL)),
                        place = cursor.getString(cursor.getColumnIndex(PLACE)),
                        user = cursor.getString(cursor.getColumnIndex(USER)),
                        deptId = cursor.getString(cursor.getColumnIndex(DEPT_ID)),
                        dept = cursor.getString(cursor.getColumnIndex(DEPT)),
                        company = cursor.getString(cursor.getColumnIndex(COMPANY)),
                        status = cursor.getInt(cursor.getColumnIndex(STATUS)) > 0
                    )
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return item
    }

    /**
     * 根据 Id和旧资产编码 获取对象
     */
    fun search(context: Context, str: String, autoCloseDb: Boolean = true): AssetsModel? {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$ASSETS_ID = ? OR $BARCODE = ?",
                arrayOf(str,str), null, null, null)
        var item: AssetsModel? = null
        if (cursor != null) {
            if (cursor.moveToFirst()){
                item = AssetsModel(
                        barcode = cursor.getString(cursor.getColumnIndex(BARCODE)),
                        assetsId = cursor.getString(cursor.getColumnIndex(ASSETS_ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME)),
                        model = cursor.getString(cursor.getColumnIndex(MODEL)),
                        place = cursor.getString(cursor.getColumnIndex(PLACE)),
                        user = cursor.getString(cursor.getColumnIndex(USER)),
                        deptId = cursor.getString(cursor.getColumnIndex(DEPT_ID)),
                        dept = cursor.getString(cursor.getColumnIndex(DEPT)),
                        company = cursor.getString(cursor.getColumnIndex(COMPANY)),
                        status = cursor.getInt(cursor.getColumnIndex(STATUS)) > 0
                )
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return item
    }

    /**
     * 是否为空
     */
    fun isEmpty(context: Context, autoCloseDb: Boolean = true): Boolean {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, null,
                arrayOf(), null, null, null, "1")
        if (cursor != null) {
            while (cursor.moveToNext()){
                return false
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return true
    }
}