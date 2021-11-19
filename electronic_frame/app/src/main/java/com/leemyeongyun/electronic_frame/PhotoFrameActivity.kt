package com.leemyeongyun.electronic_frame

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private var currentPosition = 0

    private var timer: Timer? = null

    private val photoImageView: ImageView by lazy {
        findViewById(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        getPhotoUriFromIntent()
        startTimer()

    }

    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {//let으로 null이 아닐때만 실행하게함
                photoList.add(Uri.parse(it)) //Uri 객체로 다시 변환
            }
        }
    }

    //5초에 한번씩 바뀌는 타이머
    private fun startTimer() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {
                val current = currentPosition
                val next =
                    if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1//마지막일때는 다시 첫번쨰 순서로 돌아옴

                backgroundPhotoImageView.setImageURI(photoList[current])
                photoImageView.alpha = 0f //alpha는 투명도, 0f는 완전투명해서 아예 보이지않음
                photoImageView.setImageURI(photoList[next])

                //이미지가 서서히 나타남
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000) //1초 동안 발생
                    .start()

                currentPosition = next

            }
        }
    }

    override fun onStop() {//activity가 백그라운드로 들어가서 더이상 사용되지않는 상태
        super.onStop()
        timer?.cancel()
    }

    override fun onStart() { // onStop에서는 onCreate로 넘어가지 않고, onStart로 오기때문에 여기서 타이머 시작
        super.onStart()
        startTimer()
    }

    override fun onDestroy() { //앱이 완전 종료된 상태
        super.onDestroy()
        timer?.cancel()
    }

}