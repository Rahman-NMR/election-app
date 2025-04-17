package com.rahman.pemiluapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingDecoration(
    private val defaultPadding: Int = 0,
    private val paddingBottom: Int = 0,
    private val includeHorizontal: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount?.minus(1)

        if (includeHorizontal) {
            outRect.top = defaultPadding
            outRect.left = defaultPadding
            outRect.right = defaultPadding
        } else outRect.top = defaultPadding

        if (position == itemCount) outRect.bottom = paddingBottom
    }
}