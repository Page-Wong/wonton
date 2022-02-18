package com.lsinfo.wonton.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.posapi.PosApi
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by G on 2018-05-08.
 */
abstract class Pda3506ScanReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action
        if (action!!.equals(PosApi.ACTION_POS_COMM_STATUS, ignoreCase = true)) {
            val cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1)
            val status = intent.getIntExtra(PosApi.KEY_CMD_STATUS, -1)
            val bufferLen = intent.getIntExtra(PosApi.KEY_CMD_DATA_LENGTH, 0)
            var buffer: ByteArray? = intent.getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER)

            when (cmdFlag) {
                PosApi.POS_EXPAND_SERIAL_INIT -> {
                }
                PosApi.POS_EXPAND_SERIAL3 -> {
                    if (buffer == null) return
                    val sb = StringBuffer()
                    for (i in buffer.indices) {
                        if (buffer[i].toInt() == 0x0D) {
                            sb.append("\n")
                        } else {
                            sb.append(buffer[i].toChar())
                        }
                    }
                    val serialNumber = sb.toString().trim { it <= ' ' }
                    callBack(serialNumber)
                }
            }
        }
    }

    abstract fun callBack(str: String)
}