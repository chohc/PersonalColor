package com.example.personalcolor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class Test2Activity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    private var spring: Int = -1
    private var summer: Int = -1
    private var fall: Int = -1
    private var winter: Int = -1
    var imageUri: String? = null
    var faceCropBitmap: Bitmap? = null
    private var currentFragmentIndex = 0
    private val warmFragments = listOf(
        Warm1Fragment.newInstance(),
        Warm2Fragment.newInstance(),
        Warm3Fragment.newInstance(),
        Warm4Fragment.newInstance()
    )
    private val coolFragments = listOf(
        Cool1Fragment.newInstance(),
        Cool2Fragment.newInstance(),
        Cool3Fragment.newInstance(),
        Cool4Fragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)

        // 프로그레스 바 설정
        progressBar = findViewById(R.id.progressBar)
        progressBar.max = 4  // 프로그레스 바의 최대값을 4로 설정
        progressBar.progress = 1  // 초기 프로그레스 값은 1로 설정

        // Intent에서 값을 받음
        spring = intent.getIntExtra("spring", -1)
        summer = intent.getIntExtra("summer", -1)
        fall = intent.getIntExtra("fall", -1)
        winter = intent.getIntExtra("winter", -1)
        imageUri = intent.getStringExtra("imageUri")
        faceCropBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("bitmap", Bitmap::class.java)
        } else {
            intent.getParcelableExtra("bitmap")
        }

        // 첫 번째 Fragment 표시
        showFragment(currentFragmentIndex)
    }

    private fun showFragment(index: Int) {
        // spring 값이 -1보다 크다면(WarmSurveyActivity에서 넘어왔다면) warmFragment 보여주기
        if (spring > -1) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, warmFragments[index])
                .commit()
        }
        // 그렇지 않다면 (CoolSurveyActivity에서 넘어왔다면) coolFragment 보여주기
        else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, coolFragments[index])
                .commit()
        }
    }

    fun updateSpringFall(isSpringSelected: Boolean) {
        // 선택한 이미지에 따라 spring 또는 fall 값 증가
        if (isSpringSelected) {
            spring++
        } else {
            fall++
        }

        // 다음 Fragment로 이동
        if (currentFragmentIndex < warmFragments.size - 1) {
            currentFragmentIndex++
            showFragment(currentFragmentIndex)
        }
        // 마지막 선택이었다면 결과 보여주기
        else {
            showWarmResultActivity()
        }
    }

    fun updateSummerWinter(isSpringSelected: Boolean) {
        // 선택한 이미지에 따라 summer 또는 winter 값 증가
        if (isSpringSelected) {
            summer++
        } else {
            winter++
        }

        // 다음 Fragment로 이동
        if (currentFragmentIndex < coolFragments.size - 1) {
            currentFragmentIndex++
            showFragment(currentFragmentIndex)
        }
        // 마지막 선택이었다면 결과 보여주기
        else {
            showCoolResultActivity()
        }
    }

    private fun showWarmResultActivity() {
        val intent = Intent(this, ResultActivity1::class.java)

        // 봄웜톤인지 가을웜톤인지 값 전달
        val tone = if (spring > fall) "spring" else "fall"
        intent.putExtra("tone", tone)

        // 사진 bitmap 전달
        intent.putExtra("bitmap", faceCropBitmap)

        startActivity(intent)
        finish()
    }

    private fun showCoolResultActivity() {
        val intent = Intent(this, ResultActivity1::class.java)

        // 여름쿨톤인지 겨울쿨톤인지 값 전달
        val tone = if (summer > winter) "summer" else "winter"
        intent.putExtra("tone", tone)

        // 사진 bitmap 전달
        intent.putExtra("bitmap", faceCropBitmap)

        startActivity(intent)
        finish()
    }

    fun updateProgressBar(progress: Int) {
        // 주어진 값으로 프로그레스 바 설정
        progressBar.progress = progress
    }
}