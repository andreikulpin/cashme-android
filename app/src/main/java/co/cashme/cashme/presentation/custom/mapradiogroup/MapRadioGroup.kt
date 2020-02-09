package co.cashme.cashme.presentation.custom.mapradiogroup

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.updatePadding
import co.cashme.cashme.R

class MapRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RadioGroup(context, attrs) {
    private val buttonHorizontalPadding =
        resources.getDimensionPixelSize(R.dimen.map_radio_button_padding_horizontal)

    var items: List<MapRadioButtonModel> = emptyList()
        set(value) {
            field = value
            setupButtons()
        }

    init {
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.radio_group_map_background)
        setupButtons()
    }

    private fun setupButtons() {
        items.forEach { buttonModel ->
            val button = RadioButton(context).apply {
                id = buttonModel.id
                buttonDrawable = null
                setBackgroundResource(R.drawable.radio_button_map_background)
                isChecked = buttonModel.checked
                text = buttonModel.title
                updatePadding(left = buttonHorizontalPadding, right = buttonHorizontalPadding)
            }

            addView(button)
        }
        requestLayout()
    }
}