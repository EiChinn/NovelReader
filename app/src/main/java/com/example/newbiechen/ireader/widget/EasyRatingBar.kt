package com.example.newbiechen.ireader.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.R
import com.example.newbiechen.ireader.utils.ScreenUtils
import java.lang.ref.WeakReference

/**
 * Created by newbiechen on 17-4-30.
 * 简单实现RatingBar效果
 */
class EasyRatingBar @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(mContext, attrs, defStyleAttr) {
    //默认等级的数量
    private var mRateCount: Int = 0
    //当前的等级
    private var mCurrentRate = 0
    //未选中的图片的id
    private var mNormalRes: Int = 0
    //选中的图片的id
    private var mSelectRes: Int = 0
    //间距
    private var mInterval: Int = 0

    //每个格子的宽、高
    private var mRoomWidth: Int = 0
    private var mRoomHeight: Int = 0

    private val mNormalWeak: WeakReference<Drawable>? = null
    private val mSelectWeak: WeakReference<Drawable>? = null
    private var mPaint: Paint? = null

    //返回空间的高度
    private val initRoomHeight: Int
        get() {
            val normal = getDrawable(mNormalWeak, mNormalRes)
            val select = getDrawable(mSelectWeak, mSelectRes)
            val normalMin = Math.min(normal!!.intrinsicWidth, normal.intrinsicHeight)
            val selectMin = Math.min(select!!.intrinsicWidth, select.intrinsicHeight)
            val drawableMin = Math.min(normalMin, selectMin)
            return Math.min(ScreenUtils.dpToPx(DEFAULT_MAX_HEIGHT), drawableMin)
        }

    init {
        initAttrs(attrs)
        init()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val a = mContext.obtainStyledAttributes(attrs, R.styleable.EasyRatingBar)
        mRateCount = a.getInteger(R.styleable.EasyRatingBar_rateNum, 5)
        mNormalRes = a.getResourceId(R.styleable.EasyRatingBar_rateNormal, R.drawable.rating_star_nor)
        mSelectRes = a.getResourceId(R.styleable.EasyRatingBar_rateSelect, R.drawable.rating_star_sel)
        mInterval = a.getDimension(R.styleable.EasyRatingBar_rateInterval, ScreenUtils.dpToPx(4).toFloat()).toInt()

        val currentRate = a.getInteger(R.styleable.EasyRatingBar_rating, 0)
        if (currentRate < mRateCount) {
            mCurrentRate = currentRate
        }
        a.recycle()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRoomHeight = h
        mRoomWidth = w / mRateCount
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == View.MeasureSpec.AT_MOST && heightMode == View.MeasureSpec.AT_MOST) {
            //宽度 × 大小
            val viewHeight = initRoomHeight
            val viewWidth = viewHeight * mRateCount
            widthSize = viewWidth
            heightSize = viewHeight
        } else if (widthMode == View.MeasureSpec.AT_MOST && heightMode == View.MeasureSpec.EXACTLY) {
            val viewWidth = heightSize * mRateCount
            widthSize = viewWidth
        } else if (widthMode == View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.AT_MOST) {
            heightSize = widthSize / mRateCount
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val normalDrawable = getDrawable(mNormalWeak, mNormalRes)
        val selectDrawable = getDrawable(mSelectWeak, mSelectRes)
        //绘制的半径
        val radius = Math.min(mRoomWidth, mRoomHeight) / 2 - mInterval
        //进行绘制
        for (i in 0 until mRateCount) {
            var roomWidthCenter = 0
            roomWidthCenter = when (i) {
                0 -> mRoomWidth / 2 - mInterval
                mRateCount - 1 -> mRoomWidth / 2 + mRoomWidth * i + mInterval
                else -> mRoomWidth / 2 + mRoomWidth * i
            }
            val roomHeightCenter = mRoomHeight / 2
            canvas.save()
            canvas.translate(roomWidthCenter.toFloat(), roomHeightCenter.toFloat())
            //绘制正常图片
            normalDrawable!!.setBounds(-radius, -radius, radius, radius)
            normalDrawable.draw(canvas)
            //绘制选中图片
            if (i < mCurrentRate) {
                selectDrawable!!.setBounds(-radius, -radius, radius, radius)
                selectDrawable.draw(canvas)
            }
            canvas.restore()
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //根据当前位置判断点击到哪个区域之后重置
        //暂时不做咯～～

        return super.onTouchEvent(event)

    }

    private fun getDrawable(weak: WeakReference<Drawable>?, res: Int): Drawable? {
        return if (weak?.get() == null) {
            ContextCompat.getDrawable(mContext, res)
        } else {
            weak.get()
        }
    }

    fun setRating(currentRate: Int) {
        mCurrentRate = currentRate
        invalidate()
    }


    override fun onSaveInstanceState(): Parcelable? {
        val superParcel = super.onSaveInstanceState()
        val savedState = SavedState(superParcel)
        savedState.rateCount = mRateCount
        savedState.currentRate = mCurrentRate
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mRateCount = savedState.rateCount
        mCurrentRate = savedState.currentRate
    }

    internal class SavedState : BaseSavedState {
        var rateCount: Int = 0
        var currentRate: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            rateCount = `in`.readInt()
            currentRate = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(rateCount)
            out.writeInt(currentRate)
        }

        companion object {

            @JvmField val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_MAX_HEIGHT = 48
    }
}
