package com.example.personalcolor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(private val context: Context, private val imageIds: IntArray) : BaseAdapter() {
    override fun getCount(): Int = imageIds.size
    override fun getItem(position: Int): Any = imageIds[position]
    override fun getItemId(position: Int): Long = 0
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView = convertView as? ImageView ?: ImageView(context)
        imageView.setImageResource(imageIds[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = true // 뷰 경계 조정 허용
        return imageView
    }
}