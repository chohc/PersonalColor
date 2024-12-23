package com.example.personalcolor

import android.Manifest

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.personalcolor.databinding.ActivityMainBinding
import com.example.personalcolor.databinding.ActivityTest1Binding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.text.SimpleDateFormat
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import org.opencv.android.OpenCVLoader
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class Test1Activity : BaseActivity() {

    // 권한용 요청 코드
    val PERM_STORAGE = 9
    val PERM_CAMERA = 10

    // 요청 코드
    val REQ_CAMERA = 11
    val REQ_GALLERY = 12

    // 바인딩 생성
    val binding by lazy { ActivityTest1Binding.inflate(layoutInflater) }

    // 모델 인터프리터
    private lateinit var interpreter: Interpreter

    // 결과 톤
    var tone: String = ""

    // 이미지 uri
    var imageUri: String? = null

    // 이미지 비트맵
    var faceCropBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imagePreview)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        OpenCVLoader.initDebug()

        // 모델 초기화
        interpreter = Interpreter(loadModelFile("coolwarm_cnn_model.tflite"))

        // 가이드라인 이미지 보여주기
        binding.imageView.setImageResource(R.drawable.guideline_final)

        // 1. 외부저장소 권한이 있는지 확인
        requirePermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERM_STORAGE)
    }

    fun initViews(){
        // 2. 카메라 요청 시 권한을 먼저 체크하고 승인되었으면 카메라를 연다.
        binding.cameraButton.setOnClickListener {
            requirePermission(arrayOf(Manifest.permission.CAMERA), PERM_CAMERA)
        }
        // 5. 갤러리 버튼이 클릭되면 갤러리를 연다
        binding.galleryButton.setOnClickListener {
            openGallery()
        }
        // next 버튼이 클릭되면 설문 페이지로 이동
        binding.goSurveyButton.setOnClickListener {
            if(tone == ""){
                Toast.makeText(this, "사진을 먼저 업로드해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(tone == "Cool"){
                Log.d("Test1Activity", "bitmap: $faceCropBitmap")
                var intent = Intent(this,CoolSurveyActivity::class.java)
                intent.putExtra("imageUri", imageUri)
                intent.putExtra("bitmap", faceCropBitmap)
                startActivity(intent)
            } else {
                Log.d("Test1Activity", "bitmap: $faceCropBitmap")
                var intent = Intent(this,WarmSurveyActivity::class.java)
                intent.putExtra("imageUri", imageUri)
                intent.putExtra("bitmap", faceCropBitmap)
                startActivity(intent)
            }
        }

    }

    // 원본 이미지의 주소를 저장할 변수
    var realUri: Uri? = null

    // 3. 카메라에 찍은 사진을 저장하기 위한 Uri를 넘겨준다.
    fun openCamera(){
        // 카메라 열기
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageUri(newfileName(), "image/jpg")?.let{ uri ->
            realUri = uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            startActivityForResult(intent, REQ_CAMERA)
        }
    }

    fun openGallery(){
        // 갤러리 여는 인텐트
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQ_GALLERY)
    }

    // 원본 이미지를 저장할 Uri를 MediaStore(데이터베이스)에 생성하는 메서드
    fun createImageUri(filename:String, mimeTye:String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename) // put(키,값)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeTye)

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) // MediaStore.Images.Media.EXTERNAL_CONTENT_URI은 테이블이라고 생각하면 될듯
    }

    // 파일 이름을 생성하는 메서드
    fun newfileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.jpg"
    }

    // 원본 이미지를 불러오는 메서드
    fun loadBitmap(photoUri:Uri) : Bitmap? {
        try{
            // sdk 버전별로 처리법이 다름
            return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
                val source = ImageDecoder.createSource(contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else{
                MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    // 권한 요청 승인
    override fun permissionGranted(requestCode: Int) {
        when(requestCode){
            PERM_STORAGE -> initViews()
            PERM_CAMERA -> openCamera()
        }
    }

    // 권한 요청 거절
    override fun permissionDenied(requestCode: Int) {
        when(requestCode){
            PERM_STORAGE -> {
                // API 29부터는 외부저장소 권한을 따로 요청할 필요가 X
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    permissionGranted(PERM_STORAGE)
                } else {
                    Toast.makeText(this, "공용 저장소 권한을 승인해야 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            PERM_CAMERA -> {
                Toast.makeText(this, "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // startActivityForResult의 결과값이 여기로 넘어옴
    // 이미지가 data 안에 담겨서 돌아옴
    // 4. 카메라를 찍은 후에 호출된다.
    // 6. 갤러리에서 선택 후 호출된다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){

            when(requestCode){
                REQ_CAMERA -> {
                    // ACTION_IMAGE_CAPTURE를 사용하면 이미지가 Bitmap으로 반환됨
//                    val bitmap = data?.extras?.get("data") as Bitmap  // 카메라 미리보기 이미지
//                    binding.imageView.setImageBitmap(bitmap)  // 이미지뷰에 넣어줌
                    realUri?.let { uri ->
                        val bitmap = loadBitmap(uri)
                        binding.imageView.setImageBitmap(bitmap)
                        imageUri = uri.toString()

                        if (bitmap != null) {
                            detectAndCropFace(bitmap) { processedBitmap ->
                                // 전처리된 이미지로 모델 예측
                                val resizedBitmap = Bitmap.createScaledBitmap(processedBitmap, 128, 128, true)
                                val result = predict(resizedBitmap)
                                tone = if (result[0] > result[1]) "Cool" else "Warm"
                            }
                        }

                        realUri = null
                    }
                }

                REQ_GALLERY -> {
                    data?.data?.let {uri ->
                        // 갤러리에서 가져올때는 저장할 필요가 따로 없으니까 사진의 uri만 가져오면 됨
                        // binding.imageView.setImageURI(uri)

                        // 갤러리에서 선택한 사진 처리
                        val bitmap = loadBitmap(uri)
                        binding.imageView.setImageBitmap(bitmap)
                        imageUri = uri.toString()

                        if (bitmap != null) {
                            detectAndCropFace(bitmap) { processedBitmap ->
                                // 전처리된 이미지로 모델 예측
                                val resizedBitmap = Bitmap.createScaledBitmap(processedBitmap, 128, 128, true)
                                val result = predict(resizedBitmap)
                                tone = if (result[0] > result[1]) "Cool" else "Warm"
                                Toast.makeText(this, tone, Toast.LENGTH_SHORT).show()
                                // binding.imageView.setImageBitmap(processedBitmap)
                                // intent로 imageUri 값 전달
                                intent.putExtra("imageUri", uri.toString())
                            }
                        }
                    }
                }
            }
        }
    }


    // 이미지 전처리(얼굴인식, ycbcr마스크)
    private fun detectAndCropFace(bitmap: Bitmap, callback: (Bitmap) -> Unit) {
        // 이미지를 원하는 크기로 리사이징
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // ML Kit Face Detector 초기화
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
        val faceDetector = FaceDetection.getClient(options)

        // 얼굴 감지
        val inputImage = InputImage.fromBitmap(resizedBitmap, 0)
        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0] // 첫 번째 얼굴만 처리
                    val faceRect = face.boundingBox // 얼굴 경계 상자

                    // 경계 상자 조정
                    var padding = -10 // 원하는 크기만큼 패딩
                    var left = (faceRect.left - padding).coerceAtLeast(0)
                    var top = (faceRect.top - padding).coerceAtLeast(0)
                    var right = (faceRect.right + padding).coerceAtMost(resizedBitmap.width)
                    var bottom = (faceRect.bottom + padding).coerceAtMost(resizedBitmap.height) + 5

                    // 조정된 경계 상자를 사용하여 얼굴 부분 크롭
//                    val croppedBitmap = Bitmap.createBitmap(
//                        resizedBitmap,
//                        left,
//                        top,
//                        right - left,
//                        bottom - top
//                    )
                    // 유효한 크기로 잘라내기
                    val croppedBitmap = if (right > left && bottom > top) {
                        Bitmap.createBitmap(resizedBitmap, left, top, right - left, bottom - top)
                    } else {
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    }

                    // YCbCr 마스크 적용
                    val ycbcrMaskedImage = applyYCbCrMask(croppedBitmap)

                    // 인텐트로 넘길 크롭 이미지 만들기
                    // 경계 상자 재조정
                    val paddingHosizontal = 15
                    val paddingVertical = 20 // 원하는 크기만큼 패딩
                    left = (faceRect.left - paddingHosizontal).coerceAtLeast(0)
                    top = (faceRect.top - paddingVertical).coerceAtLeast(0)
                    right = (faceRect.right + paddingHosizontal).coerceAtMost(resizedBitmap.width)
                    bottom = (faceRect.bottom + paddingVertical + 5).coerceAtMost(resizedBitmap.height) + 5

                    // 조정된 경계 상자를 사용하여 얼굴 부분 크롭
//                    val croppedBitmap2 = Bitmap.createBitmap(
//                        resizedBitmap,
//                        left,
//                        top,
//                        right - left,
//                        bottom - top
//                    )

                    // 유효한 크기로 잘라내기
                    val croppedBitmap2 = if (right > left && bottom > top) {
                        Bitmap.createBitmap(resizedBitmap, left, top, right - left, bottom - top)
                    } else {
                        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                    }

                    // 인텐트로 넘길 크롭 이미지
                    faceCropBitmap = croppedBitmap2

                    // 전처리된 이미지 반환
                    callback(ycbcrMaskedImage)
                } else {
                    // 얼굴이 감지되지 않았을 때 처리
                    Toast.makeText(this, "얼굴 인식 실패, 다른 사진을 올려주세요", Toast.LENGTH_SHORT).show()
                    callback(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                // 얼굴 감지 실패 시 에러 처리
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                callback(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
            }
    }


    // Ycbcr 마스크
    private fun applyYCbCrMask(bitmap: Bitmap): Bitmap {
        // 비트맵의 픽셀 데이터 잠금 해제
        val mutableBitmap = if (!bitmap.isMutable) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        val mat = Mat()
        Utils.bitmapToMat(mutableBitmap, mat)

        // RGB -> Ycbcr
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2YCrCb)

        // 마스크 기준
        val lowerBound = Scalar(60.0, 135.0, 85.0)
        val upperBound = Scalar(255.0, 180.0, 135.0)

        // 마스크
        val mask = Mat()
        Core.inRange(mat, lowerBound, upperBound, mask)

        // 결과 이미지를 저장할 Mat 객체 생성
        val resultMat = Mat()
        mat.copyTo(resultMat, mask)

        // Ycbcr -> RGB
        Imgproc.cvtColor(resultMat, resultMat, Imgproc.COLOR_YCrCb2RGB)

        // 결과 이미지를 비트맵으로 변환
        val ycbcrBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultMat, ycbcrBitmap)

        // Mat 객체 메모리 해제
        mat.release()
        mask.release()
        resultMat.release()

        return ycbcrBitmap
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