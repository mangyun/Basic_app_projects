package com.leemyeongyun.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizerView)
    }

    private val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.recordTimeTextView)
    }

    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }

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
        set(value) {
            field = value
            //새로운 state가 될때마다 호출, 그러면 아이콘이 바뀌게 됨
            recordButton.updateIconWithState(value)

            //리셋버튼의 사용되는 상태는 녹음 후거나, 재생 중일때임
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission() //앱을 시작하자마자 바로 권한 요청
        initViews()
        bindViews()
        initVariables()
    }

    //요청한 권한에 대한 결과를 받음
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) &&
                (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
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
        //soundVisualizerView에 recorder max값을 가져가기 위함.
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        //리셋버튼
        resetButton.setOnClickListener {
            stopPlaying()//재생중에서도 리셋을 할 수 있게함
            soundVisualizerView.clearVisualization() //음성 시각화 제거
            recordTimeTextView.clearCountTime()// 시간 제거
            state = State.BEFORE_RECORDING

        }


        //녹음버튼 클릭 시, 녹음 진행행
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

    //상태 초기화
    private fun initVariables() {
        state = State.BEFORE_RECORDING
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
        soundVisualizerView.startVisualizing(false) //replay가 아니므로 false
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    //녹음 중지
    private fun stopRecording() {
        recorder?.run {
            stop() // 중지
            release() // 메모리 해제
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    //재생 시작
    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }

        //완료처리 - 현재 전달된 파일을 전부 재생했을 때 발생
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING

        }

        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    //재생 중지
    private fun stopPlaying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    //상수값
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }

}






