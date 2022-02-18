package com.lsinfo.wonton.model

import android.content.Context
import com.lsinfo.wonton.db.InventoryAssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.db.InventoryErrorTypeDbManager

/**
 * Created by G on 2018-05-07.
 */
data class InventoryErrorModel(
        var inventoryAssetsId: String,
        var typeId: String,
        var correctInfo: String?): IDbModel
{
    override fun save(context: Context): Boolean {
        return if (InventoryErrorDbManager.get(context, inventoryAssetsId, typeId) == null){
            InventoryErrorDbManager.insert(context, this) > 0
        }
        else{
            InventoryErrorDbManager.update(context, this) > 0
        }
    }

    override fun delete(context: Context): Boolean {
        return InventoryErrorDbManager.delete(context, inventoryAssetsId, typeId) > 0
    }

    fun getInventoryAssets(context: Context): InventoryAssetsModel?{
        return InventoryAssetsDbManager.get(context, inventoryAssetsId)
    }

    fun getErrorType(context: Context): InventoryErrorTypeModel?{
        return InventoryErrorTypeDbManager.get(context, typeId)
    }
}