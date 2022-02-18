package com.lsinfo.wonton.db

import android.content.ContentValues
import android.content.Context
import com.lsinfo.wonton.model.InventoryErrorModel

/**
 * Created by G on 2018-03-19.
 */
object InventoryErrorDbManager {
    private const val INVENTORY_ASSETS_ID = "inventory_assets_id"
    private const val TYPE_ID = "type_id"
    private const val CORRECT_INFO = "correct_info"

    private const val DB_TABLE = "inventory_error"//表名

    /**
     * 新增一条数据
     */
    fun insert(context: Context, item: InventoryErrorModel?, isTransaction: Boolean = true) : Long{
        if (null == item) {
            return -1L
        }
        val db = DbHelper.getWritableDatabase(context)
        var id: Long = -1
        if (isTransaction) db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(INVENTORY_ASSETS_ID, item.inventoryAssetsId)
            values.put(TYPE_ID, item.typeId)
            values.put(CORRECT_INFO, item.correctInfo)
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
    fun update(context: Context, item: InventoryErrorModel, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            val values = ContentValues()
            values.put(CORRECT_INFO, item.correctInfo)
            count = db.update(DB_TABLE, values, "$INVENTORY_ASSETS_ID = ? AND $TYPE_ID = ?", arrayOf(item.inventoryAssetsId, item.typeId))
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
    fun delete(context: Context, assetsId: String, typeId: String, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            count = db.delete(DB_TABLE, "$INVENTORY_ASSETS_ID = ? AND $TYPE_ID = ?", arrayOf(assetsId, typeId))
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
    fun get(context: Context, inventoryAssetsId: String, typeId: String, autoCloseDb: Boolean = true): InventoryErrorModel? {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$INVENTORY_ASSETS_ID = ? AND $TYPE_ID = ?",
                arrayOf(inventoryAssetsId, typeId), null, null, null)
        var item: InventoryErrorModel? = null
        if (cursor != null) {
            if (cursor.moveToFirst()){
                item = InventoryErrorModel(
                        inventoryAssetsId = cursor.getString(cursor.getColumnIndex(INVENTORY_ASSETS_ID)),
                        typeId = cursor.getString(cursor.getColumnIndex(TYPE_ID)),
                        correctInfo = cursor.getString(cursor.getColumnIndex(CORRECT_INFO))
                    )
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return item
    }

    /**
     * 查找某盘点资产的错误信息
     */
    fun queryByInventoryAssetsId(context: Context, inventoryAssetsId: String, autoCloseDb: Boolean = true): MutableList<InventoryErrorModel> {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$INVENTORY_ASSETS_ID = ?",
                arrayOf(inventoryAssetsId), null, null, null)
        var items : MutableList<InventoryErrorModel> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()){
                val item = InventoryErrorModel(
                        inventoryAssetsId = cursor.getString(cursor.getColumnIndex(INVENTORY_ASSETS_ID)),
                        typeId = cursor.getString(cursor.getColumnIndex(TYPE_ID)),
                        correctInfo = cursor.getString(cursor.getColumnIndex(CORRECT_INFO))
                )
                items.add(item)
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return items
    }
}