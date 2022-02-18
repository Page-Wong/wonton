package com.lsinfo.wonton

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.posapi.PosApi


/**
 * Created by G on 2018-04-02.
 */
class App : Application() {

    internal var mPosApi: PosApi? = null
    private var mCurDev1 = ""

    //请求队列
    /**
     * 返回请求队列
     * @return
     */
    lateinit var requestQueue: RequestQueue
    //SharedPreferences,用于存储少量的数据
    private lateinit var _preferences: SharedPreferences

    init {
        super.onCreate()
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        //mDb = Database.getInstance(this);
        Log.v("hello", "APP onCreate~~")


        if(android.os.Build.MODEL == "3506") {
            mPosApi = PosApi.getInstance(this)
            if (mPosApi != null){
                pda3506Init()
            }
        }

        _preferences = getSharedPreferences("App", 0)
        requestQueue = Volley.newRequestQueue(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityStarted(p0: Activity?) {

                }

                override fun onActivityDestroyed(p0: Activity?) {

                }

                override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {

                }

                override fun onActivityStopped(p0: Activity?) {

                }

                override fun onActivityCreated(p0: Activity?, p1: Bundle?) {

                }

                override fun onActivityPaused(p0: Activity?) {

                }

                override fun onActivityResumed (activity: Activity){
                    mCurrentActivity = activity
                }
            });
        }
    }

    private fun pda3506Init() {
        if (Build.MODEL.equals("3508", ignoreCase = true) || Build.MODEL.equals("403", ignoreCase = true)) {
            mPosApi!!.initPosDev("ima35s09")
            mCurDev1 = "ima35s09"
        } else if (Build.MODEL.equals("5501", ignoreCase = true)) {
            mPosApi!!.initPosDev("ima35s12")
            mCurDev1 = "ima35s12"
        } else {
            mPosApi!!.initPosDev(PosApi.PRODUCT_MODEL_IMA80M01)
            mCurDev1 = PosApi.PRODUCT_MODEL_IMA80M01
        }
    }

    /**
     * 用于检测返回头中包含的cookie
     * 并且更新本地存储的cookie
     * @param headers
     */
    fun checkSessionCookie(headers: Map<String, String>) {
        if (headers.containsKey(SET_COOKIE_KEY)) {
            var cookie = headers[SET_COOKIE_KEY]
            if (!cookie.isNullOrEmpty() && !cookie!!.contains("saeut")) {
                val splitCookie = cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val splitSessionId = splitCookie[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                cookie = splitSessionId[1]
                val prefEditor = _preferences.edit()
                prefEditor.putString(COOKIE_USERNAME, cookie)
                prefEditor.apply()
            }
        }
    }

    /**
     * 向请求头中加入cookie
     * @param headers
     */
    fun addSessionCookie(headers: MutableMap<String, String>) {
        val sessionId = _preferences.getString(COOKIE_USERNAME, "")
        if (sessionId!!.isNotEmpty()) {
            val builder = StringBuilder()
            builder.append(COOKIE_USERNAME)
            builder.append("=")
            builder.append(sessionId)
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ")
                builder.append(headers[COOKIE_KEY])
            }
            headers[COOKIE_KEY] = builder.toString()
        }
    }

    companion object {

        internal var instance: App? = null

        var mCurrentActivity: Activity? = null

        //关于cookie的关键字
        private const val SET_COOKIE_KEY = "Set-Cookie"
        private const val COOKIE_KEY = "Cookie"
        private const val COOKIE_USERNAME = "JSESSIONID"

        fun getInstance(): App {
            if (instance == null) {
                instance = App()
            }
            return instance!!
        }
    }

}
