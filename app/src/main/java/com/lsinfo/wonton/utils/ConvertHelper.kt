package com.lsinfo.wonton.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.lsinfo.wonton.model.AssetsModel
import com.lsinfo.wonton.model.InventoryAssetsModel
import java.util.*

/**
 * Created by G on 2018-05-09.
 */
object ConvertHelper {
    fun assets2InventoryAssets(item: AssetsModel, planId: String): InventoryAssetsModel{
        return InventoryAssetsModel(
                isLocal = false,
                planId = planId,
                assetsId = item.assetsId,
                name = item.name,
                model = item.model,
                deptId = item.deptId,
                dept = item.dept,
                user = item.user,
                place = item.place,
                company = item.company,
                status = item.status
        )
    }

    fun newId(): String{
        return UUID.randomUUID().toString().replace("-","").substring(0, 20)
    }


    val gson : Gson = GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create()
}