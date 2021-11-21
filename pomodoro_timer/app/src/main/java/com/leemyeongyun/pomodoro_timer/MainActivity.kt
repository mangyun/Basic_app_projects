package com.leemyeongyun.pomodoro_timer

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

    private var currentCountDownTimer: CountDownTimer? = null //현재 카운트시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
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
                    currentCountDownTimer?.cancel() //현재 카운트시간을 멈춤
                    currentCountDownTimer = null

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { // 조작을 멈췄을 때, 카운트 시작

                    seekBar ?: return // 더이상 카운트다운을 진행하지않음

                    currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
                    currentCountDownTimer?.start() //중간에 코드 수정으로 언제든 null이 될수 있으므로
                }

            }
        )
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) { //1초마다 한번씩 텍스트뷰 갱신
                updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                updateRemainTimes(0) // 시간을 0으로
                updateSeekBar(0) // seekBar를 아예 왼쪽 끝으로
            }

        }

    private fun updateRemainTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60) // 한자리수일때 앞 0추가.
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()  //seekBar는 애초에 초를 고려할 필요가 없음
    }

}