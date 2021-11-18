package com.leemyeongyun.electronic_frame

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }
    private val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }

    //apply로 list 초기화
    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.imageView1_1))
            add(findViewById(R.id.imageView1_2))
            add(findViewById(R.id.imageView1_3))
            add(findViewById(R.id.imageView2_1))
            add(findViewById(R.id.imageView2_2))
            add(findViewById(R.id.imageView2_3))
        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf() //바로초기화


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }


    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    ContextCompat.checkSelfPermission( //이미 앱에 특정권한을 부여했는지 확인하기 위해
                        this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> { // 권한이 있을시, 갤러리에서 사진을 선택하는 함수
                        navigatePhotos()
                    } // 권한이 없을시, 교육용 팝업 확인 후 권한 팝업을 띄움
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        showPermissionContextPopup()

                    }
                    else -> { // 이외의 경우, 권한을 요청하는 팝업을 띄움
                        requestPermissions( //여러 권한들을 요청
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), // 그 중, READ_EXTERNAL_STORAGE이라는 하나만 요청
                            1000// 그 요청의 키값을 1000으로 지정
                        )
                    }
                }
            }
        }
    }

    //위의 requestPermissions의 콜백으로 pervmissionResult 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> { // 이전에 requestCode = 1000으로 함
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()//권한이 다시 생긴다면, 사진 선택
                } else { // 권한이 없다면
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> { // 1000번 말고는 request를 안했기 때문에 구현할게 없음

            }

        }
    }

    // 권한이 있을시, 갤러리에서 사진을 선택하는 함수
    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" //모든 이미지 파일들을 가져옴
        startActivityForResult(intent, 2000)
    }

    //위의 startActivityForResult의 콜백으로 activityResult를 처리
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // intent가 nullable인것은, 혹시라도 데이터가 없을때 오류가 발생할수있음
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) { //ok가 아니라면 그냥 종료
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data //null?처리로 인해 data 처리함.

                if (selectedImageUri != null) {

                    if ( imageUriList.size == 6){
                        Toast.makeText(this, "이미 사진이 가득 찼습니다.", Toast.LENGTH_SHORT).show()
                    }
                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            else -> { // 2000번 말고는 request를 안했기 때문에 구현할게 없음

            }
        }
    }


    //교육용 팝업 확인 후 권한 팝업을 띄우는 기능
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun initStartPhotoFrameModeButton() {

    }

}

