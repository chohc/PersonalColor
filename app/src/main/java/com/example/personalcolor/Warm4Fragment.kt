package com.example.personalcolor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class Warm4Fragment : Fragment() {
    companion object {
        fun newInstance(): Warm4Fragment {
            return Warm4Fragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 인플레이션
        val view = inflater.inflate(R.layout.fragment_warm4, container, false)

        val imageView1: ImageView = view.findViewById(R.id.imageView1)
        val imageView2: ImageView = view.findViewById(R.id.imageView2)

        // Test2Activity로부터 imageUri 가져오기
        val imageUri = (activity as? Test2Activity)?.imageUri

        // imageUri가 null이 아니라면 이미지 설정
        imageUri?.let {
            // URI를 Bitmap으로 변환하여 이미지뷰에 설정
            val bitmap = BitmapFactory.decodeFile(it)
            imageView1.setImageBitmap(bitmap)
            imageView2.setImageBitmap(bitmap)
        }

        // 옷 투명 배경 제거하고 배치
        val overlayImageView1: ImageView = view.findViewById(R.id.overlayImageView1)
        val overlayImageView2: ImageView = view.findViewById(R.id.overlayImageView2)

        setImageViewWithTransparentBackground(overlayImageView1, R.drawable.spring_blue)
        setImageViewWithTransparentBackground(overlayImageView2, R.drawable.fall_blue)

        // 이미지 누르면 spring 혹은 fall 값 증가
        imageView1.setOnClickListener { (activity as? Test2Activity)?.updateSpringFall(true) }
        imageView2.setOnClickListener { (activity as? Test2Activity)?.updateSpringFall(false) }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 프로그레스 바 값을 4로 설정
        (activity as? Test2Activity)?.updateProgressBar(4)
    }

    // 비트맵으로 이미지 처리 함수
    private fun setImageViewWithTransparentBackground(imageView: ImageView, drawableResId: Int) {
        val originalBitmap = BitmapFactory.decodeResource(resources, drawableResId)
        val croppedBitmap = cropTransparentArea(originalBitmap)
        imageView.setImageBitmap(croppedBitmap)
    }

    // 투명 배경 제거 함수
    private fun cropTransparentArea(sourceBitmap: Bitmap): Bitmap? {
        var minX = sourceBitmap.width
        var minY = sourceBitmap.height
        var maxX = -1
        var maxY = -1

        for (y in 0 until sourceBitmap.height) {
            for (x in 0 until sourceBitmap.width) {
                val alpha = (sourceBitmap.getPixel(x, y) shr 24) and 255
                if (alpha > 0) { // Not transparent
                    minX = minOf(minX, x)
                    maxX = maxOf(maxX, x)
                    minY = minOf(minY, y)
                    maxY = maxOf(maxY, y)
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return null // No non-transparent pixels found
        }

        return Bitmap.createBitmap(sourceBitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)
    }
}