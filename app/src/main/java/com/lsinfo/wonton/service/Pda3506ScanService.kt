package com.lsinfo.wonton.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Vibrator
import android.posapi.PosApi
import android.util.Log
import android.widget.Toast
import com.lsinfo.wonton.App
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by G on 2018-05-08.
 */

class Pda3506ScanService : Service() {

    private var isOpen = false
    private val mComFd = -1

    private var scanBroadcastReceiver: ScanBroadcastReceiver? = null

    internal lateinit var player: MediaPlayer

    internal var receiver_: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            val action = intent.action
            if (action!!.equals(PosApi.ACTION_POS_COMM_STATUS, ignoreCase = true)) {
                val cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1)
                var buffer: ByteArray? = intent
                        .getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER)
                when (cmdFlag) {
                    PosApi.POS_EXPAND_SERIAL_INIT -> {
                    }
                    PosApi.POS_EXPAND_SERIAL3 -> {
                        if (buffer == null)
                            return

                        if (Config.SETTING_SCAN_PLAY_BEEP) player.start()
                        try {
                            val str = String(buffer, Charset.forName("GBK"))
                            Log.e("ScanStr", "-----:" + str.trim { it <= ' ' })
                            val intentBroadcast = Intent()
                            val intentBroadcast1 = Intent()
                            intentBroadcast.action = "com.qs.scancode"
                            intentBroadcast1.action = "com.zkc.scancode"
                            intentBroadcast.putExtra("code", str.trim { it <= ' ' })
                            intentBroadcast1.putExtra("code", str.trim { it <= ' ' })
                            sendBroadcast(intentBroadcast)
                            sendBroadcast(intentBroadcast1)
                            isScan = false
                            Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 1)
                            handler.removeCallbacks(run)
                        } catch (e: UnsupportedEncodingException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }

                    }
                }
                buffer = null
            }
        }
    }

    internal var vibrator: Vibrator? = null

    internal var isScan = false

    internal var handler = Handler()
    internal var run: Runnable = Runnable {
        // TODO Auto-generated method stub
        Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 1)
        isScan = false
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onCreate() {
        // TODO Auto-generated method stub
        init()

        initGPIO()

        val mFilter = IntentFilter()
        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS)
        registerReceiver(receiver_, mFilter)

        scanBroadcastReceiver = ScanBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("ismart.intent.scandown")
        this.registerReceiver(scanBroadcastReceiver, intentFilter)

        player = MediaPlayer.create(applicationContext, R.raw.beep)

        super.onCreate()

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId)
    }

    @Throws(UnsupportedEncodingException::class)
    fun toGBK(str: String): String? {
        return this.changeCharset(str, "GBK")
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str
     * 待转换编码的字符串
     * @param newCharset
     * 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun changeCharset(str: String?, newCharset: String): String? {
        if (str != null) {
            // 用默认字符编码解码字符串。
            val bs = str.toByteArray(charset(newCharset))
            // 用新的字符编码生成字符串
            return String(bs, Charset.forName(newCharset))
        }
        return null
    }

    // private static void closeDevice() {
    // // open power
    // mApi.gpioControl(mGpioPower, 0, 0);
    // mApi.extendSerialClose(mCurSerialNo);
    // }

    private fun initGPIO() {
        // TODO Auto-generated method stub

        //		openDevice();

        Toast.makeText(applicationContext, "扫描服务初始化", Toast.LENGTH_SHORT)
                .show()

        if (mComFd > 0) {
            isOpen = true
            // readData();
        } else {
            isOpen = false
        }
    }


    @Deprecated("")
    override fun onStart(intent: Intent, startId: Int) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId)
        Handler().postDelayed({
            // TODO Auto-generated method stub
            openDevice()
        }, 1000)
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        mApi!!.closeDev()
        super.onDestroy()
    }

    internal inner class ScanBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (!isScan) {
                Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 0)
                isScan = true
                handler.removeCallbacks(run)
                handler.postDelayed(run, 3000)
            } else {
                Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 1)
                Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 0)
                isScan = true
                handler.removeCallbacks(run)
                handler.postDelayed(run, 3000)
            }
        }
    }

    companion object {
        private val SHOW_RECV_DATA = 1
        // public static ServiceBeepManager beepManager;

        var mApi: PosApi? = null

        private val mGpioPower: Byte = 0x1E// PB14
        private val mGpioTrig: Byte = 0x29// PC9

        private val mCurSerialNo = 3 // usart3
        private val mBaudrate = 4 // 9600

        fun init() {
            mApi = App.getInstance().mPosApi

            Handler().postDelayed({
                // TODO Auto-generated method stub
                openDevice()
                closeScan()
            }, 1000)
        }

        internal var isIscanScan = false

        fun openScan() {
            Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 0)
            try {
                Thread.sleep(100)
            } catch (e: Exception) {
            }

            Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 1)
        }

        fun closeScan() {
            Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 1)
            try {
                Thread.sleep(100)
            } catch (e: Exception) {
            }

            Pda3506ScanService.mApi!!.gpioControl(mGpioTrig, 0, 0)
        }

        private fun openDevice() {
            // open power
            mApi!!.gpioControl(mGpioPower, 0, 1)

            mApi!!.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1)
        }
    }

}