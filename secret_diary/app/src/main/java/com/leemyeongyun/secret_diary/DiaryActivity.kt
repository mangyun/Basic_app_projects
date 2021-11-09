package com.leemyeongyun.secret_diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity(){

    private val handler = Handler(Looper.getMainLooper()) //메인 쓰레드를 연결

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)


        val diaryEditText = findViewById<EditText>(R.id.diaryEditText)
        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)
        diaryEditText.setText(detailPreferences.getString("detail", ""))//sharedpreference에서 값을 가져와 저장

        //시간차 저장을 하기위해 인터페이스 선언
       val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit { // 여기서는 수시로 글을 저장하는 기능이기때문에, commit을 이전과 달리 false로 실행
                putString("detail", diaryEditText.text.toString())
            }
        }

        //내용이 바뀔때마다 호출되는 리스너, 멈칫할때 저장되는 시스템
        diaryEditText.addTextChangedListener {
            handler.removeCallbacks(runnable)//0.5초 이전에 있는 runnable을 지우기 위함.
            handler.postDelayed(runnable, 500) // 0.5초에 한번씩 저장

        }



   }
}
