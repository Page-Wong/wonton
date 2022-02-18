package com.lsinfo.wonton.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.HashMap

/**
 * Created by G on 2018-07-16.
 */
object BarcodeCreater {
    /**
     * 图片两端所保留的空白的宽度
     */
    private val marginW = 20
    /**
     * 条形码的编码类型
     */
    var barcodeFormat = BarcodeFormat.CODE_128

    /**
     * 生成条形码
     *
     * @param context
     * @param contents
     * 需要生成的内容
     * @param desiredWidth
     * 生成条形码的宽带
     * @param desiredHeight
     * 生成条形码的高度
     * @param displayCode
     * 是否在条形码下方显示内容
     * @return
     */
    fun creatBarcode(context: Context, contents: String,
                     desiredWidth: Int, desiredHeight: Int, displayCode: Boolean,
                     barType: Int): Bitmap? {
        var ruseltBitmap: Bitmap? = null
        if (barType == 1) {
            barcodeFormat = BarcodeFormat.CODE_128
        } else if (barType == 2) {
            barcodeFormat = BarcodeFormat.QR_CODE
        }
        if (displayCode) {
            var barcodeBitmap: Bitmap? = null
            try {
                barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                        desiredWidth, desiredHeight)
            } catch (e: WriterException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val codeBitmap = creatCodeBitmap(contents, desiredWidth,
                    desiredHeight, context)
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, PointF(
                    0f, desiredHeight.toFloat()))
        } else {
            try {
                ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
                        desiredWidth, desiredHeight)
            } catch (e: WriterException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }

        return ruseltBitmap
    }

    /**
     * 生成显示编码的Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    fun creatCodeBitmap(contents: String, width: Int,
                        height: Int, context: Context): Bitmap {
        return creatCodeBitmap(contents, width, height, 25f, Gravity.CENTER_HORIZONTAL, context)
        /*val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
                width, height)
        tv.layoutParams = layoutParams
        tv.text = contents
        //tv.setHeight(48);
        tv.textSize = 25f
        tv.gravity = Gravity.CENTER_HORIZONTAL
        tv.width = width
        tv.isDrawingCacheEnabled = true
        tv.setTextColor(Color.BLACK)
        tv.setBackgroundColor(Color.WHITE)
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)

        tv.buildDrawingCache()
        return tv.drawingCache*/
    }

    /**
     * 生成显示编码的Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param size
     * @param context
     * @return
     */
    fun creatCodeBitmap(contents: String, width: Int,
                        height: Int, size:  Float, gravity: Int, context: Context): Bitmap {
        val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
                width, height)
        tv.layoutParams = layoutParams
        tv.text = contents
        //tv.setHeight(48);
        tv.textSize = size
        tv.gravity = gravity
        tv.width = width
        tv.isDrawingCacheEnabled = true
        tv.setTextColor(Color.BLACK)
        tv.setBackgroundColor(Color.WHITE)
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)

        tv.buildDrawingCache()
        return tv.drawingCache
    }

    /**
     * 生成条形码的Bitmap
     *
     * @param contents
     * 需要生成的内容
     * @param format
     * 编码格式
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @throws WriterException
     */
    fun encode2dAsBitmap(contents: String, desiredWidth: Int,
                         desiredHeight: Int, barType: Int): Bitmap? {
        if (barType == 1) {
            barcodeFormat = BarcodeFormat.CODE_128
        } else if (barType == 2) {
            barcodeFormat = BarcodeFormat.QR_CODE
        }
        var barcodeBitmap: Bitmap? = null
        try {
            barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight)
        } catch (e: WriterException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return barcodeBitmap
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first
     * @param second
     * @param fromPoint
     * 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    fun mixtureBitmap(first: Bitmap?, second: Bitmap?,
                      fromPoint: PointF?): Bitmap? {
        if (first == null || second == null || fromPoint == null) {
            return null
        }

        val newBitmap = Bitmap.createBitmap(first.width,
                first.height + second.height, Bitmap.Config.ARGB_4444)
        val cv = Canvas(newBitmap)
        cv.drawBitmap(first, 0f, 0f, null)
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null)
        cv.save(Canvas.ALL_SAVE_FLAG)
        cv.restore()

        return newBitmap
    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(contents: String, format: BarcodeFormat,
                       desiredWidth: Int, desiredHeight: Int): Bitmap {
        val WHITE = -0x1 // 可以指定其他颜色，让二维码变成彩色效果
        val BLACK = -0x1000000

        var hints: HashMap<EncodeHintType, String>? = null
        val encoding = guessAppropriateEncoding(contents)
        if (encoding != null) {
            hints = HashMap(2)
            hints[EncodeHintType.CHARACTER_SET] = encoding
        }
        val writer = MultiFormatWriter()
        val result = writer.encode(contents, format, desiredWidth,
                desiredHeight, hints)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun guessAppropriateEncoding(contents: CharSequence): String? {
        // Very crude at the moment
        for (i in 0 until contents.length) {
            if (contents[i].toInt() > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }

    fun saveBitmap2file(bmp: Bitmap, filename: String): Boolean {
        val format = Bitmap.CompressFormat.JPEG
        val quality = 100
        var stream: OutputStream? = null
        try {
            stream = FileOutputStream("/sdcard/$filename")
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return bmp.compress(format, quality, stream)
    }
}