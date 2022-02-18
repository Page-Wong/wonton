package com.lsinfo.wonton.db

import android.content.ContentValues
import android.content.Context
import com.google.gson.Gson
import com.lsinfo.wonton.model.AssetsModel
import com.lsinfo.wonton.model.InventoryAssetsModel
import com.lsinfo.wonton.model.InventoryErrorTypeModel

/**
 * Created by G on 2018-03-19.
 */
object InventoryErrorTypeDbManager {
    private const val ID = "code"
    private const val NAME = "name"

    private const val DB_TABLE = "inventory_error_type"//表名

    /**
     * 新增一条数据
     */
    fun insert(context: Context, item: InventoryErrorTypeModel?, isTransaction: Boolean = true) : Long{
        if (null == item) {
            return -1L
        }
        val db = DbHelper.getWritableDatabase(context)
        var id: Long = -1
        if (isTransaction) db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(ID, item.id)
            values.put(NAME, item.name)
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
     * 更新一条数据
     */
    fun update(context: Context, item: InventoryErrorTypeModel, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            val values = ContentValues()
            values.put(ID, item.id)
            values.put(NAME, item.name)
            count = db.update(DB_TABLE, values, "$ID = ?", arrayOf(item.id))
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
    fun delete(context: Context, itemId: String, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            count = db.delete(DB_TABLE, "$ID = ?", arrayOf(itemId))
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
    fun get(context: Context, itemId: String, autoCloseDb: Boolean = true): InventoryErrorTypeModel? {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$ID = ?",
                arrayOf(itemId), null, null, null)
        var item: InventoryErrorTypeModel? = null
        if (cursor != null) {
            if (cursor.moveToFirst()){
                item = InventoryErrorTypeModel(
                        id = cursor.getString(cursor.getColumnIndex(ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME))
                    )
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return item
    }

    /**
     * 查找已完成但未反馈的指令
     */
    fun getAll(context: Context, autoCloseDb: Boolean = true): MutableList<InventoryErrorTypeModel> {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, null,
                arrayOf(), null, null, null)
        var items : MutableList<InventoryErrorTypeModel> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()){
                val item = InventoryErrorTypeModel(
                        id = cursor.getString(cursor.getColumnIndex(ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME))
                )
                items.add(item)
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return items
    }
}