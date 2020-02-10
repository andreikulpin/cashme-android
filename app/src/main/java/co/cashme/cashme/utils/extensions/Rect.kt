package co.cashme.cashme.utils.extensions

import android.graphics.Rect

fun Rect.update(
    left: Int = this.left,
    top: Int = this.top,
    right: Int = this.right,
    bottom: Int = this.bottom
) {
    set(left, top, right, bottom)
}