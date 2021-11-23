package com.leemyeongyun.recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View


//음성 시각화 뷰
class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null //빈 파라미터를 보내 기본값 amplitude를 전달

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { //곡선이 부드럽게 그려짐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getColor(R.color.purple_500) //색 지정
            strokeWidth = LINE_WIDTH //두께만 지정(높이는 실제값에 의하여 지정해야함)
            strokeCap = Paint.Cap.ROUND//선의 양끝을 동그랗게 처리
        }
    }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0

    //반복해서 amplitude를 가져와, 20밀리초 뒤에 그리기
    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if (!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                // main에 onRequestCurrentAmplitude호출해 main의 recorder max값을 가져옴
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
                //그릴때는 제일 처음이 오른쪽부터 시작함
            } else {
                replayingPosition += 1
            }

            invalidate() //데이터가 추가되면 갱신을 해야함

            handler?.postDelayed(this, ACTION_INTERVAL)// 자신을 20밀리초 뒤에 다시 실행
        }
    }

    //각 화면에 맞게 사이즈 조절
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h

    }

    //조건에 맞게 그리는 함수
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return //canvas가 null이면 리턴

        val centerY = drawingHeight / 2f //먼저 높이의 중앙으로
        var offsetX = drawingWidth.toFloat() //가로 시작 지점

        drawingAmplitudes.let { amplitudes ->
            if (isReplaying) { //녹음한 것을 다시 재생시킬때
                amplitudes.takeLast(replayingPosition) //가장 뒤부터 순서대로 가져옴
            } else { //녹음 중
                amplitudes //현재를 대로 가져옴
            }
        }
            .forEach { amplitude ->
                val lineLength =
                    amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F //비율에 맞게하되, 너무 꽉차지 않게 0.8 곱

                offsetX -= LINE_SPACE //옆으로 한칸씩
                if (offsetX < 0) return@forEach //뷰의 왼쪽을 넘어간다면 종료


                //그리기
                canvas.drawLine(
                    offsetX, centerY - lineLength / 2F, //선 시작점
                    offsetX, centerY + lineLength / 2F, //선 끝점
                    amplitudePaint
                )

            }

    }

    //visualizeRepeatAction 반복 호출
    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    //visualizeRepeatAction 반복 호출 중단
    fun stopVisualizing() {
        replayingPosition = 0 //재생을 반복할 경우
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList() //현재 amplitudes를 비워버림
        invalidate()

    }

    //그리는 선의 두께와 길이
    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE =
            Short.MAX_VALUE.toFloat() // 32767, 위에서 나눴을때 0을 방지하기위해 F형으로
        private const val ACTION_INTERVAL = 20L //반복 시간 20밀리초
    }
}