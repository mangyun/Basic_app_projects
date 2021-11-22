package com.leemyeongyun.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }

    private val requiredPermission = arrayOf(Manifest.permission.RECORD_AUDIO) //권한 요청 변수

    private val recordingFilePath: String by lazy { //녹음하는 파일을 저장하는 path
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var recorder: MediaRecorder? = null//오디오나 비디오같은 파일은 사용하지않을때는 메모리에서 해제
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECORDING //녹음 전 상태값
        set(value) { //새로운 state가 될때마다 호출, 그러면 아이콘이 바뀌게 됨
            field = value
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //앱을 시작하자마자 바로 권한 요청
        initViews()
        bindViews()
    }

    //요청한 권한에 대한 결과를 받음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        //부여결과 중에서 arrayOf로 전달했기때문에 firstOrNull로 하나만 전달

        if (!audioRecordPermissionGranted) { //권한이 거부되었다면
            finish() //앱 종료
        }
    }

    private fun requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(requiredPermission, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() {
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }


    //녹음 시작
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        } //녹음할 수 있는 상태를 전부 마침
        recorder?.start()
        state = State.ON_RECORDING
    }

    //녹음 중지
    private fun stopRecording() {
        recorder?.run {
            stop() // 중지
            release() // 메모리 해제
        }
        recorder = null
        state = State.AFTER_RECORDING
    }

    //재생 시작
    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.start()
        state = State.ON_PLAYING
    }

    //재생 중지
    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
    }

    //상수값
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }


}






