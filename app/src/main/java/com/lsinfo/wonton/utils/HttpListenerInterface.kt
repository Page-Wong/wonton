package com.lsinfo.wonton.utils

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.lsinfo.wonton.model.ResultCode
import com.lsinfo.wonton.model.ResultModel
import org.json.JSONObject

/**
 * Created by G on 2018-04-02.
 */
abstract class HttpListenerInterface(val context: Context) : VolleyListenerInterface(context){

    var result = ResultModel()

    override fun onMyError(error: VolleyError) {
        //TODO G 将错误写入日志
        Log.e("test", error.toString())
        onError(error)
    }

    override fun onMySuccess(json: String) {
        try {
            val obj = JSONObject(json)
            if (obj.get("success") as Boolean) {
                try{
                    onSuccess(obj)
                }
                catch (e:Exception){
                    result.code = ResultCode.OPERATE_ERROR
                    result.exception = e
                }
            } else {
                //TODO G 将错误写入日志
                onFail(obj)
            }
        }
        catch (e: Exception){
            result.code = ResultCode.HTTP_ERROR
            result.exception = e
        }
        finally {
            onCallBack(result)
            //TODO G 写入通讯日志等操作
        }
    }

    open fun onSuccess(json: JSONObject) {}

    open fun onFail(json: JSONObject){}

    open fun onError(error: VolleyError){
        result.code = ResultCode.OPERATE_ERROR
        result.exception = error
        onCallBack(result)
    }

    open fun onCallBack(result: ResultModel){}
}