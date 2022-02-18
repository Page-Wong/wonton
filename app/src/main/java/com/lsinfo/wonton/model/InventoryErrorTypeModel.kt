package com.lsinfo.wonton.model

import android.content.Context
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.db.InventoryErrorTypeDbManager

/**
 * Created by G on 2018-05-07.
 */
data class InventoryErrorTypeModel(
        var id: String,
        var name: String): IDbModel
{
    override fun save(context: Context): Boolean {
        return if (InventoryErrorTypeDbManager.get(context, id) == null){
            InventoryErrorTypeDbManager.insert(context, this) > 0
        }
        else{
            InventoryErrorTypeDbManager.update(context, this) > 0
        }
    }

    override fun delete(context: Context): Boolean {
        return InventoryErrorTypeDbManager.delete(context, id) > 0
    }

}