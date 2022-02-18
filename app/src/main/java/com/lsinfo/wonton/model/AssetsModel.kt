package com.lsinfo.wonton.model

import android.content.Context
import com.lsinfo.wonton.db.AssetsDbManager

/**
 * Created by G on 2018-05-07.
 */
data class AssetsModel(
        val barcode: String,
        val assetsId: String,
        val name: String,
        val model: String,
        val deptId:String,
        val dept:String,
        val user: String,
        val place: String,
        val company: String,
        val status: Boolean): IDbModel
{

    override fun save(context: Context): Boolean {
        return if (AssetsDbManager.get(context, assetsId) == null){
            AssetsDbManager.insert(context, this) > 0
        }
        else{
            AssetsDbManager.update(context, this) > 0
        }
    }

    override fun delete(context: Context): Boolean {
        return AssetsDbManager.delete(context, assetsId) > 0
    }

}