/*
 * Copyright 2017 SamuelGjk. https://github.com/SamuelGjk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package moe.yukinoneko.micbottombar

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by SamuelGjk on 2017/11/23.
 */
class MICBottomBar : LinearLayout, View.OnClickListener {

    private val DEFAULT_ICON_SIZE: Int = dp2px(24f)
    private val DEFAULT_TEXT_SIZE: Int = sp2px(12f)
    private val DEFAULT_PADDING: Int = dp2px(5f)
    private val DEFAULT_SELECT_TEXT_COLOR: Int = 0xFF222222.toInt()
    private val ALPHA: Int = 0x60

    private var mIconSize: Int = 0
    private var mTextSize: Int = 0
    private var mSelectTextColor: Int = 0
    private var mTextColor: Int = 0
    private var mPadding: Int = 0
    private var mCurrentPosition: Int = 0

    private var mListener: OnNavigationItemClickListener? = null

    private val mItems: MutableList<NavigationItem> = mutableListOf()
    private val mIcons: MutableMap<Int, Drawable> = mutableMapOf()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.MICBottomNavigationBar, defStyleAttr, 0)

        mIconSize = a.getDimensionPixelSize(R.styleable.MICBottomNavigationBar_bb_icon_size, DEFAULT_ICON_SIZE)
        mTextSize = a.getDimensionPixelSize(R.styleable.MICBottomNavigationBar_bb_text_size, DEFAULT_TEXT_SIZE)
        mSelectTextColor = a.getColor(R.styleable.MICBottomNavigationBar_bb_select_text_color,
                DEFAULT_SELECT_TEXT_COLOR)
        mTextColor = a.getColor(R.styleable.MICBottomNavigationBar_bb_text_color, alpha(mSelectTextColor, ALPHA))
        mPadding = a.getDimensionPixelSize(R.styleable.MICBottomNavigationBar_bb_padding, DEFAULT_PADDING)
        mCurrentPosition = a.getInt(R.styleable.MICBottomNavigationBar_bb_current_position, 0)

        a.recycle()

        orientation = HORIZONTAL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutParams.width = LayoutParams.MATCH_PARENT
        layoutParams.height = LayoutParams.WRAP_CONTENT
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        removeAllViews()
    }

    override fun setOrientation(orientation: Int) {
        // Empty
        // Keep the orientation is horizontal
    }

    fun addNavigationItems(vararg items: NavigationItem) {
        mItems.addAll(items)
        weightSum = mItems.size.toFloat()

        items.forEach {
            addView(
                    TextView(context).apply {
                        layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                        gravity = Gravity.CENTER_HORIZONTAL
                        setPadding(0, mPadding, 0, mPadding)
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                        setCompoundDrawables(null, inflateIcon(it.iconId), null, null)
                        compoundDrawablePadding = dp2px(3f)
                        setTextColor(mTextColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
                        text = it.name
                        setOnClickListener(this@MICBottomBar)
                    }
            )
        }

        updateView(true)
    }

    override fun onClick(v: View) {
        val clickIndex = indexOfChild(v)
        setCurrentItem(clickIndex)
        mListener?.onItemClick(v, clickIndex)
    }

    fun setCurrentItem(position: Int) {
        if (position != mCurrentPosition) {
            updateView(false)
            mCurrentPosition = position
            updateView(true)
        }
    }

    private fun inflateIcon(id: Int): Drawable = mIcons[id] ?:
            ContextCompat.getDrawable(context, id).apply {
                val ratio = intrinsicHeight / intrinsicWidth.toFloat()
                // adjust height
                setBounds(0, 0, mIconSize, (mIconSize * ratio).toInt())
                mIcons.put(id, this)
            }

    // Update current selected item's view
    private fun updateView(isSelect: Boolean) {
        (getChildAt(mCurrentPosition) as TextView).run {
            setTextColor(if (isSelect) mSelectTextColor else mTextColor)
            setCompoundDrawables(
                    null,
                    inflateIcon(mItems[mCurrentPosition].run { if (isSelect) selectIconId else iconId }),
                    null,
                    null
            )
        }
    }

    fun setOnNavigationItemClickListener(listener: OnNavigationItemClickListener) {
        mListener = listener
    }

    private fun dp2px(dp: Float): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    private fun sp2px(dp: Float): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, resources.displayMetrics).toInt()

    private fun alpha(color: Int, alpha: Int): Int {
        require(alpha in 0..0xFF)
        return color and 0x00FFFFFF or (alpha shl 24)
    }

    interface OnNavigationItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class NavigationItem(@DrawableRes val iconId: Int, @DrawableRes val selectIconId: Int, val name: String)
}