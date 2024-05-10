package com.example.personalcolor

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.personalcolor.databinding.ActivitySurveyBinding
import com.example.personalcolor.databinding.ActivityTest1Binding

class SurveyActivity : AppCompatActivity() {

    // 바인딩 생성
    val binding by lazy { ActivitySurveyBinding.inflate(layoutInflater) }

    // 봄, 여름, 가을, 겨울
    var spring: Int = 0
    var summer: Int = 0
    var fall: Int = 0
    var winter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_survey)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 인공지능(Test1Activity) 결과 가져오기
        // 결과에 따라 계절에 100점 부여
        var personalColor = intent.getStringExtra("personalColor")
        when(personalColor){
            "spring" -> spring += 100
            "summer" -> summer += 100
            "fall" -> fall += 100
            "winter" -> winter += 100
        }

        // 다음 버튼을 누르면
        binding.nextButton.setOnClickListener {
            // 봄, 여름, 가을, 겨울 판단
            // 문항 5개 -> 한 문항당 20, 총점 100
            binding.RdoGroup1.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId){
                    R.id.radioButton11 -> spring += 20
                    R.id.radioButton12 -> summer += 20
                    R.id.radioButton13 -> fall += 20
                    R.id.radioButton14 -> winter += 20
                }
            }

            // 봄, 여름, 가을, 겨울 인텐트에 담기
            var intent = Intent(this,Test1Activity::class.java)
            intent.putExtra("spring", spring)
            intent.putExtra("summer", summer)
            intent.putExtra("fall", fall)
            intent.putExtra("winter", winter)
            startActivity(intent)
        }
    }
}