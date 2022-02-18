package com.lsinfo.wonton

import android.content.pm.ActivityInfo
import android.preference.PreferenceManager
import java.io.File


/**
 * Created by G on 2018-03-14.
 */
object Config {
//region 系统设置
    /**
     * 扫描成功是否有声音
     */
    var SETTING_SCAN_PLAY_BEEP : Boolean = PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getBoolean("setting_scan_play_beep", true)
//endregion


    var LOGIN_USER : String = PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString("login_user", "")

//region 其他设置
    /** 日期时间格式化 **/
    const val DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"

//endregion
        
//region     HTTP请求Host和TAG
    /** 服务器IP地址*/
    val HTTP_HOST = "http://192.168.0.7:888/web"

    val API_LOGIN = "$HTTP_HOST/system/login.action"
    val API_TAG_LOGIN = "login"

    val API_SYNC_INVENTORY_PLAN = "$HTTP_HOST/wonton/wonton!syncInventoryPlan.action"
    val API_TAG_SYNC_INVENTORY_PLAN = "syncInventoryPlan"

    val API_UPLOAD_INVENTORY_ASSETS = "$HTTP_HOST/wonton/wonton!uploadInventoryAssets.action"
    val API_TAG_UPLOAD_INVENTORY_ASSETS = "uploadInventoryAssets"

    val API_DOWNLOAD_INVENTORY_ASSETS = "$HTTP_HOST/wonton/wonton!downloadInventoryAssets.action"
    val API_TAG_DOWNLOAD_INVENTORY_ASSETS = "downloadInventoryAssets"

    val API_DOWNLOAD_ASSETS = "$HTTP_HOST/wonton/wonton!downloadAssets.action"
    val API_TAG_DOWNLOAD_ASSETS = "downloadAssets"

    val API_DOWNLOAD_INVENTORY_ERROR_TYPE = "$HTTP_HOST/wonton/wonton!downloadInventoryErrorType.action"
    val API_TAG_DOWNLOAD_INVENTORY_ERROR_TYPE = "downloadInventoryErrorType"


//endregion

}