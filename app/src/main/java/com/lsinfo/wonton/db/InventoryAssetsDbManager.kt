package com.lsinfo.wonton.db

import android.content.ContentValues
import android.content.Context
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.model.InventoryAssetsModel
import java.text.SimpleDateFormat

/**
 * Created by G on 2018-03-19.
 */
object InventoryAssetsDbManager {
    private const val ID = "pk_id"
    private const val IS_LOCAL = "isLocal"
    private const val ASSETS_ID = "assets_id"
    private const val PLAN_ID = "plan_id"
    private const val NAME = "name"
    private const val MODEL = "model"
    private const val DEPT_ID = "deptId"
    private const val DEPT = "dept"
    private const val USER = "user"
    private const val PLACE = "place"
    private const val STATUS = "status"
    private const val COMPANY = "company"
    private const val CREATE_TIME = "create_time"
    private const val CREATE_USER = "create_user"
    private const val MEMO = "memo"

    private const val DB_TABLE = "inventory_assets"//表名

    /**
     * 新增一条数据
     */
    fun insert(context: Context, item: InventoryAssetsModel?, isTransaction: Boolean = true) : Long{
        if (null == item) {
            return -1L
        }
        val db = DbHelper.getWritableDatabase(context)
        var id: Long = -1
        if (isTransaction) db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(ID, item.id)
            values.put(IS_LOCAL, item.isLocal)
            values.put(ASSETS_ID, item.assetsId)
            values.put(PLAN_ID, item.planId)
            values.put(NAME, item.name)
            values.put(MODEL, item.model)
            values.put(DEPT_ID, item.deptId)
            values.put(DEPT, item.dept)
            values.put(USER, item.user)
            values.put(PLACE, item.place)
            values.put(STATUS, item.status)
            values.put(COMPANY, item.company)
            values.put(CREATE_TIME, SimpleDateFormat(Config.DATE_TIME_FORMAT_PATTERN).format(item.createTime))
            values.put(CREATE_USER, item.createUser)
            values.put(MEMO, item.memo)
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
    fun update(context: Context, item: InventoryAssetsModel, isTransaction: Boolean = true): Int {
        val db = DbHelper.getWritableDatabase(context)
        val count: Int
        try {
            if (isTransaction) db.beginTransaction()
            val values = ContentValues()
            values.put(IS_LOCAL, item.isLocal)
            values.put(ASSETS_ID, item.assetsId)
            values.put(PLAN_ID, item.planId)
            values.put(NAME, item.name)
            values.put(MODEL, item.model)
            values.put(DEPT_ID, item.deptId)
            values.put(DEPT, item.dept)
            values.put(USER, item.user)
            values.put(PLACE, item.place)
            values.put(STATUS, item.status)
            values.put(COMPANY, item.company)
            values.put(MEMO, item.memo)
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
    fun get(context: Context, itemId: String, autoCloseDb: Boolean = true): InventoryAssetsModel? {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$ID = ?",
                arrayOf(itemId), null, null, null)
        var item: InventoryAssetsModel? = null
        if (cursor != null) {
            if (cursor.moveToFirst()){
                item = InventoryAssetsModel(
                        id = cursor.getString(cursor.getColumnIndex(ID)),
                        isLocal = cursor.getInt(cursor.getColumnIndex(IS_LOCAL))>0,
                        assetsId = cursor.getString(cursor.getColumnIndex(ASSETS_ID)),
                        planId = cursor.getString(cursor.getColumnIndex(PLAN_ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME)),
                        model = cursor.getString(cursor.getColumnIndex(MODEL)),
                        place = cursor.getString(cursor.getColumnIndex(PLACE)),
                        user = cursor.getString(cursor.getColumnIndex(USER)),
                        deptId = cursor.getString(cursor.getColumnIndex(DEPT_ID)),
                        dept = cursor.getString(cursor.getColumnIndex(DEPT)),
                        company = cursor.getString(cursor.getColumnIndex(COMPANY)),
                        status = cursor.getInt(cursor.getColumnIndex(STATUS)) > 0,
                        createTime =SimpleDateFormat(Config.DATE_TIME_FORMAT_PATTERN).parse(cursor.getString(cursor.getColumnIndex(CREATE_TIME))),
                        createUser = cursor.getString(cursor.getColumnIndex(CREATE_USER)),
                        memo = cursor.getString(cursor.getColumnIndex(MEMO))
                    )
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return item
    }

    /**
     * 查找计划内的资产
     */
    fun queryByPlan(context: Context, planId: String, autoCloseDb: Boolean = true): MutableList<InventoryAssetsModel> {
        val db = DbHelper.getReadableDatabase(context)
        val cursor = db.query(DB_TABLE, null, "$PLAN_ID = ?",
                arrayOf(planId), null, null, null)
        var items : MutableList<InventoryAssetsModel> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()){
                val item = InventoryAssetsModel(
                        id = cursor.getString(cursor.getColumnIndex(ID)),
                        isLocal = cursor.getInt(cursor.getColumnIndex(IS_LOCAL))>0,
                        assetsId = cursor.getString(cursor.getColumnIndex(ASSETS_ID)),
                        planId = cursor.getString(cursor.getColumnIndex(PLAN_ID)),
                        name = cursor.getString(cursor.getColumnIndex(NAME)),
                        model = cursor.getString(cursor.getColumnIndex(MODEL)),
                        place = cursor.getString(cursor.getColumnIndex(PLACE)),
                        user = cursor.getString(cursor.getColumnIndex(USER)),
                        deptId = cursor.getString(cursor.getColumnIndex(DEPT_ID)),
                        dept = cursor.getString(cursor.getColumnIndex(DEPT)),
                        company = cursor.getString(cursor.getColumnIndex(COMPANY)),
                        status = cursor.getInt(cursor.getColumnIndex(STATUS)) > 0,
                        createTime =SimpleDateFormat(Config.DATE_TIME_FORMAT_PATTERN).parse(cursor.getString(cursor.getColumnIndex(CREATE_TIME))),
                        createUser = cursor.getString(cursor.getColumnIndex(CREATE_USER)),
                        memo = cursor.getString(cursor.getColumnIndex(MEMO))
                )
                items.add(item)
            }
            cursor.close()
        }
        if (autoCloseDb) db.close()
        return items
    }

}