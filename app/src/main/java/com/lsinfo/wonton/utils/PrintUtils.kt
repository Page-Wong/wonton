package com.lsinfo.wonton.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.posapi.PosApi
import android.posapi.PrintQueue
import android.view.Gravity
import android.widget.Toast
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.model.InventoryAssetsModel
import com.lsinfo.wonton.service.Pda3506ScanService
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by G on 2018-07-16.
 */
object PrintUtils {
    private var mPrintQueue: PrintQueue? = null
    private var isCanPrint = true

    private fun init(c: Context) {
        //if (mPrintQueue != null) return
        mPrintQueue = PrintQueue(c, Pda3506ScanService.mApi)
        mPrintQueue!!.init()
        mPrintQueue!!.setOnPrintListener(object : PrintQueue.OnPrintListener {

            override fun onFinish() {
                // TODO Auto-generated method stub

                Toast.makeText(c.applicationContext,
                        c.getString(R.string.print_complete), Toast.LENGTH_SHORT)
                        .show()

                isCanPrint = true
            }

            override fun onFailed(state: Int) {
                // TODO Auto-generated method stub
                isCanPrint = true
                when (state) {
                    PosApi.ERR_POS_PRINT_NO_PAPER ->
                        // 打印缺纸
                        showTipDialog(c, c.getString(R.string.print_no_paper))
                    PosApi.ERR_POS_PRINT_FAILED ->
                        // 打印失败
                        showTipDialog(c, c.getString(R.string.print_failed))
                    PosApi.ERR_POS_PRINT_VOLTAGE_LOW ->
                        // 电压过低
                        showTipDialog(c, c.getString(R.string.print_voltate_low))
                    PosApi.ERR_POS_PRINT_VOLTAGE_HIGH ->
                        // 电压过高
                        showTipDialog(c, c.getString(R.string.print_voltate_high))
                }
                // Toast.makeText(PrintBarcodeActivity.this, "打印失败  错误码:"+state,
                // Toast.LENGTH_SHORT).show();
            }

            override fun onGetState(arg0: Int) {
                // TODO Auto-generated method stub

            }

            override fun onPrinterSetting(state: Int) {
                // TODO Auto-generated method stub
                isCanPrint = true
                when (state) {
                    0 -> Toast.makeText(c, "持续有纸", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(c, "缺纸", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(c, "检测到黑标", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun printSpace(context: Context){
        if(android.os.Build.MODEL != "3506") {
            Toast.makeText(context, "此设备不支持打印", Toast.LENGTH_SHORT).show()
            return
        }
        init(context)
        try {
            val textBitmap = BarcodeCreater.creatCodeBitmap("\n", 10, 5,18f, Gravity.LEFT, context)
            mPrintQueue!!.addBmp(40, 0, textBitmap.width,
                    textBitmap.height, BitmapTools.bitmap2PrinterBytes(textBitmap))
            mPrintQueue!!.printStart()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun printSimpleTag(context: Context, item: InventoryAssetsModel?) {
        if(android.os.Build.MODEL != "3506") {
            Toast.makeText(context, "此设备不支持打印", Toast.LENGTH_SHORT).show()
            return
        }
        if (item == null) {
            Toast.makeText(context, "没有可打印的信息", Toast.LENGTH_SHORT).show()
            return
        }
        init(context)
        try {
            val concentration = 40
            val mWidth = 300
            val mHeight = 60

            /*val errors = InventoryErrorDbManager.queryByInventoryAssetsId(context, item.id)
            val errorInfo = errors.map { mapOf("correctInfo" to it.correctInfo, "errorName" to it.getErrorType(context)?.name) }
            var statusStr = errorInfo.find { it["errorName"] == "应用状态调整" }?.get("correctInfo")
            statusStr = if (statusStr.isNullOrEmpty()) {if(item.status) "(闲置)" else "(在用)" } else statusStr

            var deptStr = errorInfo.find { it["errorName"] == "使用部门转移" }?.get("correctInfo")
            deptStr = if (deptStr.isNullOrEmpty()) {if(item.dept.isNullOrEmpty()) "(使用部门为空)" else item.dept } else deptStr

            var userStr = errorInfo.find { it["errorName"] == "使用人员转移" }?.get("correctInfo")
            userStr = if (userStr.isNullOrEmpty()) {if(item.user.isNullOrEmpty()) "(使用人为空)" else item.user } else userStr

            var placeStr = errorInfo.find { it["errorName"] == "存放地点调整" }?.get("correctInfo")
            placeStr = if (placeStr.isNullOrEmpty()) {if(item.place.isNullOrEmpty()) "(使用地点为空)" else item.place } else placeStr

            val sb = StringBuilder()
            sb.append("广东众生药业股份有限公司")
            sb.append("\n")
            sb.append(statusStr)
            sb.append(if(item.name.isNullOrEmpty()) "(资产名称为空)" else item.name + " / ")
            sb.append(if(item.model.isNullOrEmpty()) "(资产型号为空)" else item.model)
            sb.append("\n")
            sb.append("使用部门：$deptStr")
            sb.append("\n")
            sb.append("使用人：$userStr")
            sb.append("\n")
            sb.append("使用地点：$placeStr")
            sb.append("\n")
            val textBitmap = BarcodeCreater.creatCodeBitmap(sb.toString(), mWidth, 100,18f, Gravity.LEFT, context)
            mPrintQueue!!.addBmp(concentration, 60, textBitmap.width,
                    textBitmap.height, BitmapTools.bitmap2PrinterBytes(textBitmap))*/

            // 打印资产信息
            val sb = StringBuilder()
            sb.append("广东众生药业股份有限公司")
            sb.append("\n")
            sb.append(if(item.status) "(闲置)" else "(在用)")
            sb.append(if(item.name.isNullOrEmpty()) "(资产名称为空)" else item.name + " / ")
            sb.append(if(item.model.isNullOrEmpty()) "(资产型号为空)" else item.model)
            sb.append("\n")
            sb.append(if(item.dept.isNullOrEmpty()) "(使用部门为空)" else item.dept + " / ")
            sb.append(if(item.user.isNullOrEmpty()) "(使用人为空)" else item.user)
            sb.append("\n")
            sb.append("使用地点：" + if(item.place.isNullOrEmpty()) "(使用地点为空)" else item.place)
            sb.append("\n")
            sb.append("-------------------------------")
            sb.append("异常信息：")
            sb.append("\n")
            val errors = InventoryErrorDbManager.queryByInventoryAssetsId(context, item.id)
            if (errors.all { it.correctInfo.isNullOrEmpty() }){
                sb.append("此资产没有异常")
                sb.append("\n")
            }
            else{
                errors.forEach {
                    if(!it.correctInfo.isNullOrEmpty()){
                        sb.append("${it.getErrorType(context)?.name}：${it.correctInfo}")
                        sb.append("\n")
                    }
                }
            }

            if(item.assetsId == "999"){
                sb.append("备注：${item.memo}")
                sb.append("\n")
            }
            var text = sb.toString().toByteArray(charset("GBK"))
            addPrintTextWithSize(1, concentration, text)
//            val textBitmap = BarcodeCreater.creatCodeBitmap(sb.toString(), mWidth, 100,18f, context)
//            mPrintQueue!!.addBmp(concentration, 60, textBitmap.width,
//                    textBitmap.height, BitmapTools.bitmap2PrinterBytes(textBitmap))

            //打印条形码
            val mBitmap = BarcodeCreater.creatBarcode(context,
                    item.assetsId, mWidth, mHeight, true, 1)
            val printData = BitmapTools.bitmap2PrinterBytes(mBitmap!!)
            mPrintQueue!!.addBmp(concentration, 30, mBitmap.width,
                    mBitmap.height, printData)


            //打印间隔，用于切纸
            val spaceSb = StringBuilder()
            spaceSb.append("  张贴日期：" + SimpleDateFormat(Config.DATE_FORMAT_PATTERN).format(Date()))
            spaceSb.append("\n")
            spaceSb.append("盘点方:东莞泷晟信息科技有限公司")
            spaceSb.append("\n")
            spaceSb.append("\n")
            spaceSb.append("\n")
            spaceSb.append("\n")
            spaceSb.append("\n")
            spaceSb.append("\n")
//            val spaceBitmap = BarcodeCreater.creatCodeBitmap(spaceSb.toString(), mWidth, 40,14f, Gravity.CENTER_HORIZONTAL, context)
//            mPrintQueue!!.addBmp(concentration, 30, spaceBitmap.width,
//                    spaceBitmap.height, BitmapTools.bitmap2PrinterBytes(spaceBitmap))
            var spaceText = spaceSb.toString().toByteArray(charset("GBK"))
            addPrintTextWithSize(1, concentration, spaceText)

            mPrintQueue!!.printStart()
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    /*
	 * 打印文字 size 1 --倍大小 2--2倍大小
	 */
    private fun addPrintTextWithSize(size: Int, concentration: Int, data: ByteArray?) {
        if (data == null)
            return
        // 2倍字体大小
        val _2x = byteArrayOf(0x1b, 0x57, 0x02)
        // 1倍字体大小
        val _1x = byteArrayOf(0x1b, 0x57, 0x01)
        var mData: ByteArray? = null
        if (size == 1) {
            mData = ByteArray(3 + data.size)
            // 1倍字体大小 默认
            System.arraycopy(_1x, 0, mData, 0, _1x.size)
            System.arraycopy(data, 0, mData, _1x.size, data.size)
            mPrintQueue!!.addText(concentration, mData)
        } else if (size == 2) {
            mData = ByteArray(3 + data.size)
            // 1倍字体大小 默认
            System.arraycopy(_2x, 0, mData, 0, _2x.size)
            System.arraycopy(data, 0, mData, _2x.size, data.size)

            mPrintQueue!!.addText(concentration, mData)

        }

    }

    private fun showTipDialog(context: Context, message: CharSequence) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("" + message)
        builder.setTitle("" + context.getString(R.string.tips))
        builder.setPositiveButton("" + context.getString(R.string.confirm)) { dialog, which -> dialog.dismiss() }

        builder.create().show()
    }
}