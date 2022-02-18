package com.lsinfo.wonton.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.posapi.PosApi
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by G on 2018-05-08.
 */
abstract class PdaI6200SScanReceiver: BroadcastReceiver() {
    internal lateinit var player: MediaPlayer
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action

        player = MediaPlayer.create(context, R.raw.beep)
        if (Config.SETTING_SCAN_PLAY_BEEP) player.start()

        val barcode = intent.getByteArrayExtra("barocode")

        val barocodelen = intent.getIntExtra("length", 0)
        val temp = intent.getByteExtra("barcodeType", 0.toByte())
        android.util.Log.i("debug", "----codetype--$temp")
        val barcodeStr = String(barcode, 0, barocodelen)
        callBack(barcodeStr)
    }

    abstract fun callBack(str: String)
}