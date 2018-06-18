package com.orcchg.ecologymap

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ImageAdapter(imageIds: List<Int> = emptyList()) : PagerAdapter() {

    var images: List<Int> = emptyList()
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        images = imageIds
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = ImageView(container.context)
        image.setImageResource(images[position])
        container.addView(image)
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
}
