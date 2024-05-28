package com.example.personalcolor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.personalcolor.databinding.ActivityTfliteTestBinding
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TfliteTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTfliteTestBinding
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTfliteTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 모델 초기화
        interpreter = Interpreter(loadModelFile("coolwarm_cnn_model.tflite"))

        // 버튼 클릭 리스너 설정
        binding.predictButton.setOnClickListener {
            // 이미지를 비트맵으로 로드하고 예측 수행
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tflite_test_image_1)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
            val result = predict(resizedBitmap)
            binding.resultTextView.text = if (result[0] > result[1]) "Cool" else "Warm"
        }
    }

    // 모델 파일 로드
    private fun loadModelFile(filename: String): ByteBuffer {
        val assetFileDescriptor = assets.openFd(filename)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).order(ByteOrder.nativeOrder())
    }

    // 이미지 예측
    private fun predict(bitmap: Bitmap): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(4 * 128 * 128 * 3).order(ByteOrder.nativeOrder())
        val intValues = IntArray(128 * 128)
        bitmap.getPixels(intValues, 0, 128, 0, 0, 128, 128)

        // 이미지를 정규화하고 버퍼에 넣기
        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        val outputBuffer = ByteBuffer.allocateDirect(4 * 2).order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val result = FloatArray(2)
        outputBuffer.asFloatBuffer().get(result)
        return result
    }

    override fun onDestroy() {
        interpreter.close()
        super.onDestroy()
    }
}