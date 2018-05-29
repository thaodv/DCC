/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.wexchain.android.dcc.view.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import io.wexchain.dcc.R

/**
 * draw divider lines both top & bottom
 */
class DividerDecoration(context: Context) : RecyclerView.ItemDecoration() {

    var dividerHeight = context.resources.getDimensionPixelSize(R.dimen.divider_stroke_width)
    var dividerColor = ContextCompat.getColor(context, R.color.light_gray_alpha)

    private val mBounds = Rect()
    private val paint = Paint()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        paint.color = dividerColor

        val childCount = parent.childCount
        //first top
        if (childCount > 0) {
            val child0 = parent.getChildAt(0)
            parent.getDecoratedBoundsWithMargins(child0, mBounds)
            val top = mBounds.top + Math.round(child0.translationY)
            val bottom = top + dividerHeight
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
        //bottoms
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - dividerHeight
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State?
    ) {
        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.set(0, dividerHeight, 0, dividerHeight)
        } else {
            outRect.set(0, 0, 0, dividerHeight)
        }
    }
}
