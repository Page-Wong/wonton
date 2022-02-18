package com.lsinfo.wonton.model

import android.content.Context
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.db.InventoryAssetsDbManager
import com.lsinfo.wonton.utils.ConvertHelper
import java.util.*


/**
 * Created by G on 2018-05-07.
 */
data class InventoryAssetsModel(
        var isLocal: Boolean,
        val planId: String,
        val assetsId: String,
        val name: String,
        val model: String,
        val deptId:String,
        val dept:String,
        val user: String,
        val place: String,
        val company: String,
        var status: Boolean,
        var memo: String = "",
        val createTime: Date = Date(),
        val createUser: String = Config.LOGIN_USER,
        var id: String = ""
): IDbModel
{


    override fun save(context: Context): Boolean {
        return if (InventoryAssetsDbManager.get(context, id) == null){
            if (id.isNullOrEmpty()) id = ConvertHelper.newId()
            InventoryAssetsDbManager.insert(context, this) > 0
        }
        else{
            InventoryAssetsDbManager.update(context, this) > 0
        }
    }

    override fun delete(context: Context): Boolean {
        return InventoryAssetsDbManager.delete(context, id) > 0
    }

}