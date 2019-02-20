package com.example.newbiechen.ireader.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.newbiechen.ireader.R

class SelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val parentGroup: ViewGroup
    init {
        parentGroup = this
        orientation = HORIZONTAL
    }

    fun setSelectData(vararg selectType: List<String>) {
        for (i in selectType.indices) {
            createChildView(i, selectType[i])
        }
    }

    private fun createChildView(flag: Int, types: List<String>) {
        val item = SelectItem(context)
        val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        params.weight = 1f
        item.layoutParams = params
        item.tag = flag
        item.setData(types)

        addView(item)
    }

    private var mListener: OnItemSelectedListener? = null


    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        mListener = listener
    }

    interface OnItemSelectedListener {
        /**
         * @param type:选中的类型
         * @param pos:类型中的位置
         * 位置都是从0开始的
         */
        fun onItemSelected(type: Int, pos: Int)
    }


    private inner class SelectItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
        : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener,
            AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {
        private lateinit var tvSelected: TextView
        private lateinit var ivArrow: ImageView
        private lateinit var popupWindow: ListPopupWindow
        private lateinit var popupAdapter: SelectorAdapter

        private val typeList = mutableListOf<String>()

        private lateinit var rotateAnim: Animation
        private lateinit var restoreAnim: Animation

        private var isOpen = false

        init {
            initView()
            initWidget()
            initClick()
        }

        private fun initView() {
            val view = LayoutInflater.from(context)
                    .inflate(R.layout.view_selector, this, false)
            addView(view)

            tvSelected = view.findViewById(R.id.selector_tv_selected)
            ivArrow = view.findViewById(R.id.selector_iv_arrow)
            ivArrow.scaleType = ImageView.ScaleType.MATRIX
        }
        private fun initWidget() {
            setUpAnim()
        }
        private fun setUpAnim() {

            rotateAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_0_to_180)
            restoreAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_180_to_360)

            rotateAnim.interpolator = LinearInterpolator()
            restoreAnim.interpolator = LinearInterpolator()
            rotateAnim.fillAfter = true
            restoreAnim.fillAfter = true
        }

        private fun openPopWindow() {
            if (!this::popupWindow.isInitialized) {
                createPopWindow()
            }
            popupWindow.show()
        }

        private fun createPopWindow() {
            popupWindow = ListPopupWindow(context)
            popupAdapter = SelectorAdapter()
            popupWindow.anchorView = parentGroup.getChildAt(0)
            popupWindow.setAdapter(popupAdapter)
            popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
            popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
            //获取焦点
            popupWindow.isModal = true

            popupWindow.setOnItemClickListener(this)
            popupWindow.setOnDismissListener(this)
        }

        private fun closePopWindow() {
            if (this::popupWindow.isInitialized && popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        private fun initClick() {
            setOnClickListener(this)
        }

        internal fun setData(types: List<String>) {
            typeList.addAll(types)
            tvSelected.text = typeList[0]
        }

        override fun onClick(v: View?) {
            if (isOpen) {
                closePopWindow()
                isOpen = false
                ivArrow.startAnimation(restoreAnim)
            } else {
                openPopWindow()
                isOpen = true
                ivArrow.startAnimation(rotateAnim)
            }
        }

        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //切换text
            tvSelected.text = typeList[position]
            //设置监听器
            if (mListener != null) {
                mListener!!.onItemSelected(tag as Int, position)
            }
            popupAdapter.current = position
            popupWindow.dismiss()
        }

        override fun onDismiss() {
            if (isOpen) {
                isOpen = false
                ivArrow.startAnimation(restoreAnim)
            }
        }

        private inner class SelectorAdapter : BaseAdapter() {
            internal var current = 0

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val holder: ViewHolder
                val returnView: View
                if (convertView == null) {
                    returnView = LayoutInflater.from(context)
                            .inflate(R.layout.item_selector, parent, false)
                    holder = ViewHolder()
                    holder.tvName = returnView.findViewById(R.id.selector_tv_type)
                    returnView.tag = holder
                } else {
                    returnView = convertView
                    holder = convertView.tag as ViewHolder
                }
                if (current == position) {
                    holder.tvName!!.setTextColor(ContextCompat.getColor(context, R.color.nb_popup_text_selected))
                } else {
                    holder.tvName!!.setTextColor(ContextCompat.getColor(context, R.color.nb_text_default))
                }
                holder.tvName!!.text = typeList[position]
                return returnView
            }

            override fun getItem(position: Int)= typeList[position]

            override fun getItemId(position: Int) = position.toLong()

            override fun getCount() = typeList.size

            private inner class ViewHolder {
                internal var tvName: TextView? = null
            }

        }

    }
}