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
class SpacingDecoration(context: Context) : RecyclerView.ItemDecoration() {

    var spacingHeight = context.resources.getDimensionPixelSize(R.dimen.divider_stroke_width)

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State?
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.set(0, spacingHeight, 0, 0)
        }
    }
}
