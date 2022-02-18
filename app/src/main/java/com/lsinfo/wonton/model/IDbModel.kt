package com.lsinfo.wonton.model

import android.content.Context

/**
 * Created by G on 2018-05-08.
 */
interface IDbModel {
    fun save(context: Context): Boolean
    fun delete(context: Context): Boolean
}