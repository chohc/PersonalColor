package com.example.personalcolor

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Test2Activity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        progressBar = findViewById(R.id.progressBar)
        progressBar.max = 4  // 프로그레스 바의 최대값을 2로 설정합니다.
        progressBar.progress = 1  // 초기 프로그레스 값은 1입니다.

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, Color1Fragment())
                .commit()
        }
    }

    fun updateProgressBar(progress: Int) {
        progressBar.progress = progress  // 주어진 값으로 프로그레스를 설정합니다.
    }
}