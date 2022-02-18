package com.lsinfo.wonton.utils

import android.graphics.*

/**
 * Created by G on 2018-07-16.
 */
object BitmapTools {
    fun resizeImage(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleWidth)
        return Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true)
    }

    fun PrintImageNew(bitmapCode: Bitmap) {
        // TODO Auto-generated method stub
        val w = bitmapCode.width
        val h = bitmapCode.height
        //byte[] sendbuf = StartBmpToPrintCode(bitmapCode);

        //write(sendbuf);

    }


    fun convertToBlackWhite(bmp: Bitmap): Bitmap {
        val width = bmp.width // 获取位图的宽
        val height = bmp.height // 获取位图的高
        val pixels = IntArray(width * height) // 通过位图的大小创建像素点数组
        var b: Byte
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)
        val alpha = 0xFF shl 24
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                //grey = alpha | (grey << 16) | (grey << 8) | grey;
                if (grey < 128) {
                    b = 1
                } else {
                    b = 0
                }
                //pixels[width * i + j] = grey;
                pixels[width * i + j] = b.toInt()
            }
        }
        val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBmp
    }

    fun bitmap2Gray(bmSrc: Bitmap): Bitmap {
        // 得到图片的长和宽
        val width = bmSrc.width
        val height = bmSrc.height
        // 创建目标灰度图像
        var bmpGray: Bitmap? = null
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        // 创建画布
        val c = Canvas(bmpGray!!)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmSrc, 0f, 0f, paint)
        return bmpGray
    }

    // 该函数实现对图像进行二值化处理
    fun gray2Binary(graymap: Bitmap): Bitmap {
        //得到图形的宽度和长度
        val width = graymap.width
        val height = graymap.height
        //创建二值化图像
        var binarymap: Bitmap? = null
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true)
        //依次循环，对图像的像素进行处理
        for (i in 0 until width) {
            for (j in 0 until height) {
                //得到当前像素的值
                val col = binarymap!!.getPixel(i, j)
                //得到alpha通道的值
                val alpha = col and -0x1000000
                //得到图像的像素RGB的值
                val red = col and 0x00FF0000 shr 16
                val green = col and 0x0000FF00 shr 8
                val blue = col and 0x000000FF
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                var gray = (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
                //对图像进行二值化处理
                if (gray <= 95) {
                    gray = 0
                } else {
                    gray = 255
                }
                // 新的ARGB
                val newColor = alpha or (gray shl 16) or (gray shl 8) or gray
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor)
            }
        }
        return binarymap
    }

    fun binarization(img: Bitmap): Bitmap {
        val width = img.width
        val height = img.height
        val area = width * height
        val gray = Array(width) { IntArray(height) }
        var average = 0// 灰度平均值
        var graysum = 0
        var graymean = 0
        var grayfrontmean = 0
        var graybackmean = 0
        var pixelGray: Int
        var front = 0
        var back = 0
        val pix = IntArray(width * height)
        img.getPixels(pix, 0, width, 0, 0, width, height)
        for (i in 1 until width) { // 不算边界行和列，为避免越界
            for (j in 1 until height) {
                val x = j * width + i
                val r = pix[x] shr 16 and 0xff
                val g = pix[x] shr 8 and 0xff
                val b = pix[x] and 0xff
                pixelGray = (0.3 * r + 0.59 * g + 0.11 * b).toInt()// 计算每个坐标点的灰度
                gray[i][j] = (pixelGray shl 16) + (pixelGray shl 8) + pixelGray
                graysum += pixelGray
            }
        }
        graymean = graysum / area// 整个图的灰度平均值
        average = graymean
        //Log.i(TAG,"Average:"+average);
        for (i in 0 until width)
        // 计算整个图的二值化阈值
        {
            for (j in 0 until height) {
                if (gray[i][j] and 0x0000ff < graymean) {
                    graybackmean += gray[i][j] and 0x0000ff
                    back++
                } else {
                    grayfrontmean += gray[i][j] and 0x0000ff
                    front++
                }
            }
        }
        val frontvalue = grayfrontmean / front// 前景中心
        val backvalue = graybackmean / back// 背景中心
        val G = FloatArray(frontvalue - backvalue + 1)// 方差数组
        var s = 0
        //Log.i(TAG,"Front:"+front+"**Frontvalue:"+frontvalue+"**Backvalue:"+backvalue);
        for (i1 in backvalue until frontvalue + 1)
        // 以前景中心和背景中心为区间采用大津法算法（OTSU算法）
        {
            back = 0
            front = 0
            grayfrontmean = 0
            graybackmean = 0
            for (i in 0 until width) {
                for (j in 0 until height) {
                    if (gray[i][j] and 0x0000ff < i1 + 1) {
                        graybackmean += gray[i][j] and 0x0000ff
                        back++
                    } else {
                        grayfrontmean += gray[i][j] and 0x0000ff
                        front++
                    }
                }
            }
            grayfrontmean = grayfrontmean / front
            graybackmean = graybackmean / back
            G[s] = (back.toFloat() / area * (graybackmean - average).toFloat()
                    * (graybackmean - average).toFloat()) + (front.toFloat() / area
                    * (grayfrontmean - average).toFloat() * (grayfrontmean - average).toFloat())
            s++
        }
        var max = G[0]
        var index = 0
        for (i in 1 until frontvalue - backvalue + 1) {
            if (max < G[i]) {
                max = G[i]
                index = i
            }
        }

        for (i in 0 until width) {
            for (j in 0 until height) {
                val `in` = j * width + i
                if (gray[i][j] and 0x0000ff < index + backvalue) {
                    pix[`in`] = Color.rgb(0, 0, 0)
                } else {
                    pix[`in`] = Color.rgb(255, 255, 255)
                }
            }
        }

        val temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        temp.setPixels(pix, 0, width, 0, 0, width, height)
        return temp
        //image.setImageBitmap(temp);
    }


    fun encodeYUV420SP(yuv420sp: ByteArray, rgba: IntArray, width: Int, height: Int) {
        val frameSize = width * height

        val U = IntArray(frameSize)
        val V = IntArray(frameSize)
        val uvwidth = width / 2

        val bits = 8
        var index = 0
        var f = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                val r = rgba[index] and -0x1000000 shr 24
                val g = rgba[index] and 0xFF0000 shr 16
                val b = rgba[index] and 0xFF00 shr 8

                val y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                val u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                val v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128

                val temp = (if (y > 255) 255 else if (y < 0) 0 else y).toByte()
                yuv420sp[index++] = (if (temp > 0) 1 else 0).toByte()


                //				{
                //					if (f == 0) {
                //						yuv420sp[index++] = 0;
                //						f = 1;
                //					} else {
                //						yuv420sp[index++] = 1;
                //						f = 0;
                //					}
                //				}

            }

        }

        f = 0
    }


    fun bitmap2PrinterBytes(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        //Log.v("hello", "height?:"+height);
        val startX = 0
        val startY = 0
        val offset = 0
        var writeNo = 0
        var rgb = 0
        var colorValue = 0
        val rgbArray = IntArray(offset + (height - startY) * width
                + (width - startX))
        bitmap.getPixels(rgbArray, offset, width, startX, startY,
                width, height)

        var iCount = height % 8
        if (iCount > 0) {
            iCount = height / 8 + 1
        } else {
            iCount = height / 8
        }

        val mData = ByteArray(iCount * width)

        //Log.v("hello", "myiCount?:"+iCoun t);
        for (l in 0..iCount - 1) {
            //Log.v("hello", "iCount?:"+l);
            //Log.d("hello", "l?:"+l);
            for (i in 0 until width) {
                val rowBegin = l * 8
                //Log.v("hello", "width?:"+i);
                var tmpValue = 0
                var leftPos = 7
                val newheight = (l + 1) * 8 - 1
                //Log.v("hello", "newheight?:"+newheight);
                for (j in rowBegin..newheight) {
                    //Log.v("hello", "width?:"+i+"  rowBegin?:"+j);
                    if (j >= height) {
                        colorValue = 0
                    } else {
                        rgb = rgbArray[offset + (j - startY) * width + (i - startX)]
                        if (rgb == -1) {
                            colorValue = 0
                        } else {
                            colorValue = 1
                        }
                    }
                    //Log.d("hello", "rgbArray?:"+(offset + (j - startY)
                    //		* scansize + (i - startX)));
                    //Log.d("hello", "colorValue?:"+colorValue);
                    tmpValue = tmpValue + (colorValue shl leftPos)
                    leftPos = leftPos - 1

                }
                mData[writeNo] = tmpValue.toByte()
                writeNo++
            }
        }

        return mData
    }
}