package com.example.personalcolor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.personalcolor.databinding.ActivityCoolsurveyBinding
import com.example.personalcolor.databinding.ActivityTest1Binding

class CoolSurveyActivity : AppCompatActivity() {

    // 갑자기 바인딩이 잘안돼서 노가다로...
    lateinit var nextBtn : Button
    lateinit var RdoGroup1 : RadioGroup
    lateinit var RdoGroup2 : RadioGroup
    lateinit var RdoGroup3 : RadioGroup
    lateinit var RdoGroup4 : RadioGroup

    lateinit var radioButton11 : RadioButton
    lateinit var radioButton12 : RadioButton
    lateinit var radioButton21 : RadioButton
    lateinit var radioButton22 : RadioButton
    lateinit var radioButton31 : RadioButton
    lateinit var radioButton32 : RadioButton
    lateinit var radioButton41 : RadioButton
    lateinit var radioButton42 : RadioButton
    lateinit var radioButton51 : RadioButton
    lateinit var radioButton52 : RadioButton
    lateinit var radioButton53 : RadioButton
    lateinit var radioButton54 : RadioButton

    // 톤 점수
    var summer: Int = 0;
    var winter: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coolsurvey)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nextBtn = findViewById(R.id.goTest2Button)
        RdoGroup1 = findViewById<RadioGroup>(R.id.RdoGroup1)
        RdoGroup2 = findViewById<RadioGroup>(R.id.RdoGroup2)
        RdoGroup3 = findViewById<RadioGroup>(R.id.RdoGroup3)
        RdoGroup4 = findViewById<RadioGroup>(R.id.RdoGroup4)
        radioButton11 = findViewById(R.id.radioButton11)
        radioButton12 = findViewById(R.id.radioButton12)
        radioButton21 = findViewById(R.id.radioButton21)
        radioButton22 = findViewById(R.id.radioButton22)
        radioButton31 = findViewById(R.id.radioButton31)
        radioButton32 = findViewById(R.id.radioButton32)
        radioButton41 = findViewById(R.id.radioButton41)
        radioButton42 = findViewById(R.id.radioButton42)
        radioButton51 = findViewById(R.id.radioButton51)
        radioButton52 = findViewById(R.id.radioButton52)
        radioButton53 = findViewById(R.id.radioButton53)
        radioButton54 = findViewById(R.id.radioButton54)

        // Group5 RadioButton 클릭 리스너
        val radioButtons = listOf(radioButton51, radioButton52, radioButton53, radioButton54)

        radioButtons.forEach { button ->
            button.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                button.isChecked = true
            }
        }


        // 다음 버튼을 누르면
        nextBtn.setOnClickListener {

            // 체크 안된게 있는지 확인
            val rdoGroups = listOf(RdoGroup1, RdoGroup2, RdoGroup3, RdoGroup4)
            var allChecked = true

            for (rdoGroup in rdoGroups) {
                if (rdoGroup.checkedRadioButtonId == -1) {
                    allChecked = false
                    break
                }
            }

            // 수동으로 관리할 RdoGroup5의 radioButton들
            val radioButtonIds = listOf(R.id.radioButton51, R.id.radioButton52, R.id.radioButton53, R.id.radioButton54)
            var radioButtonChecked = false
            for (id in radioButtonIds) {
                if (findViewById<RadioButton>(id).isChecked) {
                    radioButtonChecked = true
                    break
                }
            }

            // 체크가 안됐으면 토스트메시지, 다 됐으면 다음 액티비티로 이동
            if (!allChecked || !radioButtonChecked) {
                Toast.makeText(this, "모든 질문에 답변해 주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                // 여름, 겨울 판단
                when {
                    radioButton11.isChecked -> summer += 1
                    radioButton12.isChecked -> winter += 1
                }
                when {
                    radioButton21.isChecked -> summer += 1
                    radioButton22.isChecked -> winter += 1
                }
                when {
                    radioButton31.isChecked -> summer += 1
                    radioButton32.isChecked -> winter += 1
                }
                when {
                    radioButton41.isChecked -> summer += 1
                    radioButton42.isChecked -> winter += 1
                }
                when {
                    radioButton51.isChecked -> summer += 1
                    radioButton52.isChecked -> summer += 1
                    radioButton53.isChecked -> winter += 1
                    radioButton54.isChecked -> winter += 1
                }


                // 봄, 여름, 가을, 겨울 인텐트에 담기
                var intent = Intent(this,Test2Activity::class.java)
                val tone = if (summer > winter) "summer" else "winter"
                intent.putExtra("tone", tone)
                startActivity(intent)

                // Toast.makeText(this, "summer : " + summer + " winter : " + winter + " tone : " + tone, Toast.LENGTH_SHORT).show()
            }
        }
    }
}