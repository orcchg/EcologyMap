package com.orcchg.ecologymap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_preview.*

class ImagePreviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_IDS = "extra_image_ids"

        fun getCallingIntent(context: Context, imageIds: List<Int>): Intent {
            val list = ArrayList<Int>(); list.addAll(imageIds)
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putIntegerArrayListExtra(EXTRA_IMAGE_IDS, list)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imageIds = intent.getIntegerArrayListExtra(EXTRA_IMAGE_IDS)
        val imageAdapter = ImageAdapter(imageIds)
        view_pager.adapter = imageAdapter
    }
}
