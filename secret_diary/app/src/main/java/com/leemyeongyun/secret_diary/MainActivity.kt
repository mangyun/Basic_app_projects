package com.leemyeongyun.secret_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    //lazy로 선언한 이유는 onCreate함수보다 먼저 선언했기 때문
    //여기서는 apply를 이용해 lazy변수에 바로 min, max로 초기화함.
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        numberPicker1
        numberPicker2
        numberPicker3

        //password를 저장하는 방법은 로컬DB나 파일에 직접 저장
        //여기서는 SharedPreferences를 이용
        openButton.setOnClickListener {


            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            //이 파일을 다른 앱에서 사용못하게 MODE_PRIVATE으로 선언
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)

            //맵에서 키 : value 형식이라 생각하면 됨
            if (passwordPreferences.getString("password", "000")
                    .equals(passwordFromUser)
            ) {//패스워드 성공
                startActivity(Intent(this, DiaryActivity::class.java))//패스워드가 맞으면 diaryActivity실행
            } else {//패스워드 실패
                showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)

            if (changePasswordMode) {//번호변경 후 저장
                //Preferences를 편집하기 위해 edit 이용
                //원래는 commit을 잊으면 실제로 저장이 되지않았지만, ktx를 통해 edit안에서 람다형식으로 불상사를 막음.
                passwordPreferences.edit(true) {
                    putString("password", passwordFromUser)
                }
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)


            } else {//  비밀번호가 맞을 때, changePasswordMode를 true로 활성화
                //openButton의 리스터에서 그대로 따옴

                if (passwordPreferences.getString("password", "000")
                        .equals(passwordFromUser)
                ) { // 비번이 맞으면
                    changePasswordMode = true // Mode를 활성화
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)

                } else {//패스워드 실패
                    showErrorAlertDialog()
                }
            }
        }
    }

    //에러 표시 함수
    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { dialog, which -> }//원래는 dialog, which지만 불필요한 코드생략으로 인해 _, _ 표현
            .create()
            .show()
    }
}