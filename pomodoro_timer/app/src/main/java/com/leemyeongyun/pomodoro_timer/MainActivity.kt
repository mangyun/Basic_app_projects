package com.leemyeongyun.pomodoro_timer

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null //현재 카운트시간
    private var tickingSoundId: Int? = null //째깍째깍 소리
    private var bellSoundId: Int? = null //타이머 벨 소리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume() //autoResume로 모든 활성화함수를 실행
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause() //autoPause로 모든 활성화함수를 정지
    }


    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 로드된 사운드 파일들이 사용하지 않을때 해제됨.
    }


    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener( //setOnSeekBarChangeListener의 3개 콜백 함수
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) { //사용자가 실제로 조작했을때만 업데이트
                        updateRemainTimes(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) { // 다시 조작시킬 때
                    stopCountDown() //카운트 정지
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { // 조작을 멈췄을 때, 카운트 시작

                    seekBar ?: return // 더이상 카운트다운을 진행하지않음

                    if (seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown() //카운트 시작하는 함수
                    }
                }
            }


        )
    }

    private fun initSounds() { //사운드 함수
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1) //나중에 호환성을 위해 우선순위 1로 지정
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) { //1초마다 한번씩 텍스트뷰 갱신
                updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown() // 카운트를 마쳐, 시간을 0으로 seekBar를 왼쪽으로, 벨 울리기
            }
        }


    //카운트 시작하는 함수
    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start() //중간에 코드 수정으로 언제든 null이 될수 있으므로

        // null?인 인자일 경우, null이 아닐경우에만 let으로 호출하는 함수
        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
            //좌우 볼륨, 동시에 재생하는게 아니기 때문에 우선순위는 0, 무한반복 loop -1, 재생속도

        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel() //현재 카운트시간을 멈춤
        currentCountDownTimer = null

        soundPool.autoPause()
    }

    //카운트를 마친 함수
    private fun completeCountDown() {
        updateRemainTimes(0) // 시간을 0으로
        updateSeekBar(0) // seekBar를 아예 왼쪽 끝으로

        soundPool.autoPause() // 끝났을 때, 벨 울리기
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }


    private fun updateRemainTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60) // 한자리수일때 앞 0추가.
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()  //seekBar는 애초에 초를 고려할 필요가 없음
    }

}