package com.leemyeongyun.recorder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton

//아이콘을 변경하는 함수
class RecordButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> { // 녹음 전
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {// 녹음 중
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> { // 녹음 후
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {// 재생 중중
                setImageResource(R.drawable.ic_stop)

            }
        }

    }

}