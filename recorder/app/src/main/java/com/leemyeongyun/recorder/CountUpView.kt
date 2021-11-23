package com.leemyeongyun.recorder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

//시간 반영뷰
class CountUpView(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L
    private val countUpAction: Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime() //현재시간 스탬프

            val countTimeSeconds = ((currentTimeStamp - startTimeStamp) / 1000L).toInt()// 초 반영
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this, 1000L) //1초뒤 다시 호출
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()//시작시간 스탬프

        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime() {
        updateCountTime(0)
    }

    private fun updateCountTime(countTimeSeconds: Int) {
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}