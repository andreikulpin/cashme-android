package co.cashme.cashme.presentation.feature.map.mapper

import android.content.Context
import android.graphics.*
import androidx.annotation.DrawableRes
import co.cashme.cashme.R
import co.cashme.cashme.domain.model.Rate
import javax.inject.Inject

class MarkerBitmapDelegate @Inject constructor(
    private val context: Context
) {
    private val greenIcon: Bitmap by lazy { getReourceBitmap(R.drawable.pin_green) }
    private val yellowIcon: Bitmap by lazy { getReourceBitmap(R.drawable.pin_yellow) }
    private val redIcon: Bitmap by lazy { getReourceBitmap(R.drawable.pin_red) }

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.map_marker_text_size)
    }

    private val srcMarkerRect: Rect by lazy {
        Rect(0, 0, greenIcon.width, greenIcon.height)
    }
    private val dstMarkerRect: Rect by lazy {
        val markerHeight = context.resources.getDimension(R.dimen.map_marker_height)
        val markerWidth = (markerHeight / greenIcon.height) * greenIcon.width
        Rect(0, 0, markerWidth.toInt(), markerHeight.toInt())
    }

    fun makeMarkerBitmap(rateCost: Rate.Cost): Bitmap {
        val background = when (rateCost.profit) {
            Rate.Profit.BEST -> greenIcon
            Rate.Profit.MEDIUM -> yellowIcon
            Rate.Profit.WORST -> redIcon
        }

        val bitmap = Bitmap.createBitmap(dstMarkerRect.width(), dstMarkerRect.height(), Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            drawBitmap(background, srcMarkerRect, dstMarkerRect, paint)
            drawText(
                rateCost.value.toString(),
                dstMarkerRect.width() * 0.39f,
                dstMarkerRect.height() * 0.35f,
                textPaint)
        }
        return bitmap
    }

    private fun getReourceBitmap(@DrawableRes resId: Int) =
        BitmapFactory.decodeResource(context.resources, resId)
}