package gal.uvigo.taskmanager

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip

@BindingAdapter("categoryText")
fun setCategoryText(chip: Chip, category: Category) {
    val context = chip.context
    val text = when (category) {
        Category.WORK -> context.getString(R.string.category_work)
        Category.PERSONAL -> context.getString(R.string.category_personal)
        Category.URGENT -> context.getString(R.string.category_urgent)
        Category.FAMILY -> context.getString(R.string.category_family)
        Category.SHOPPING -> context.getString(R.string.category_shopping)
        Category.OTHER -> context.getString(R.string.category_other)
    }
    chip.text = text
}

