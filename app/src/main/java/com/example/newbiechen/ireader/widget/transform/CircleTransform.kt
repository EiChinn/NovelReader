package com.example.newbiechen.ireader.widget.transform

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class CircleTransform(context: Context) : BitmapTransformation(context) {
    override fun getId(): String {
        return javaClass.name
    }

    /**
     *
     * @param pool : 图片池，这个之后会谈到。
     * @param toTransform:需要进行处理的图片
     * @param outWidth:图片的宽
     * @param outHeight:图片的高
     * @return 返回处理完的图片
     */
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val paint = Paint()
        //初始化画笔
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.isDither = true

        val width = toTransform.width
        val height = toTransform.height
        val size = Math.min(width, height)
        val x = (width - size) / 2
        val y = (height - size) / 2
        var result: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        //
        val canvas = Canvas(result!!)
        val radius = size / 2
        canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(toTransform, (-x).toFloat(), (-y).toFloat(), paint)
        return result
    }

}