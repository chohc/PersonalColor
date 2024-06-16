package com.example.personalcolor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView

class ResultActivity1 : AppCompatActivity() {
    private var faceCropBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 톤에 따라 다른 액티비티 띄움
        var tone = intent.getStringExtra("tone")
        when(tone){
            "spring" -> setContentView(R.layout.activity_result1)
            "summer" -> setContentView(R.layout.activity_result2)
            "fall" -> setContentView(R.layout.activity_result3)
            "winter" -> setContentView(R.layout.activity_result4)
        }
//        setContentView(R.layout.activity_result1)

        // Intent로부터 Bitmap 수신
        faceCropBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bitmap", Bitmap::class.java)
        } else {
            intent.getParcelableExtra("bitmap")
        }

        // ImageButton 클릭 리스너 설정
        val questionButton: ImageButton = findViewById(R.id.questionButton)
        questionButton.setOnClickListener {
            showPopup()
        }

        // 다시하기 버튼
        val retryButton: Button = findViewById(R.id.retryButton)
        retryButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // 모든 이전 액티비티를 종료하고 새로 시작
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // 나가기 버튼
        val exitButton: Button = findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            finishAffinity() // 현재 액티비티 및 모든 부모 액티비티를 종료
            System.exit(0) // 앱 프로세스 종료
        }
    }

    private fun showPopup() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_result, null)
        builder.setView(dialogView)

        val imageView1 = dialogView.findViewById<ImageView>(R.id.imageView1_dialog)
        val imageView2 = dialogView.findViewById<ImageView>(R.id.imageView2_dialog)
        val imageView3 = dialogView.findViewById<ImageView>(R.id.imageView3_dialog)
        val imageView4 = dialogView.findViewById<ImageView>(R.id.imageView4_dialog)
        val imageView5 = dialogView.findViewById<ImageView>(R.id.imageView5_dialog)
        val imageView6 = dialogView.findViewById<ImageView>(R.id.imageView6_dialog)

        val imageViews = arrayOf(imageView1, imageView2, imageView3, imageView4, imageView5, imageView6)

        // 각 imageView_dialog에 faceCropBitmap 적용
        faceCropBitmap?.let {
            for (imageView in imageViews) {
                imageView.setImageBitmap(it)
            }
        }

        // 색상 뷰를 가져오기
        val colorView1 = findViewById<View>(R.id.color1)
        val colorView2 = findViewById<View>(R.id.color2)
        val colorView3 = findViewById<View>(R.id.color3)
        val colorView4 = findViewById<View>(R.id.color4)
        val colorView5 = findViewById<View>(R.id.color5)
        val colorView6 = findViewById<View>(R.id.color6)

        val colorViews = arrayOf(colorView1, colorView2, colorView3, colorView4, colorView5, colorView6)

        // overlayImageView에 투명 배경 제거 및 색상 입히기 적용
        val overlayImageView1 = dialogView.findViewById<ImageView>(R.id.overlayImageView1)
        val overlayImageView2 = dialogView.findViewById<ImageView>(R.id.overlayImageView2)
        val overlayImageView3 = dialogView.findViewById<ImageView>(R.id.overlayImageView3)
        val overlayImageView4 = dialogView.findViewById<ImageView>(R.id.overlayImageView4)
        val overlayImageView5 = dialogView.findViewById<ImageView>(R.id.overlayImageView5)
        val overlayImageView6 = dialogView.findViewById<ImageView>(R.id.overlayImageView6)

        val overlayImageViews = arrayOf(overlayImageView1, overlayImageView2, overlayImageView3, overlayImageView4, overlayImageView5, overlayImageView6)

        for (i in overlayImageViews.indices) {
            val overlayDrawable = resources.getDrawable(R.drawable.white_clothes, null)
            val overlayBitmap = (overlayDrawable as BitmapDrawable).bitmap
            setImageViewWithTransparentBackground(overlayImageViews[i], overlayBitmap)

            // 색상 값 가져와서 tint 적용
            val color = (colorViews[i].background as ColorDrawable).color
            overlayImageViews[i].setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }

        // 위쪽으로 이동시키기
        overlayImageView1.translationY = -33f // 위로 32만큼 이동
        overlayImageView2.translationY = -33f // 위로 32만큼 이동
        overlayImageView3.translationY = -33f // 위로 32만큼 이동
        overlayImageView4.translationY = -33f // 위로 32만큼 이동
        overlayImageView5.translationY = -33f // 위로 32만큼 이동
        overlayImageView6.translationY = -33f // 위로 32만큼 이동

        // 확인 버튼 작동
        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
                true
            } else {
                false
            }
        }
    }

    // 투명 배경 제거 및 비트맵 적용 함수
    private fun setImageViewWithTransparentBackground(imageView: ImageView, sourceBitmap: Bitmap) {
        val croppedBitmap = cropTransparentArea(sourceBitmap)
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



