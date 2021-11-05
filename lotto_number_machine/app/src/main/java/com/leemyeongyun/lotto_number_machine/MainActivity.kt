package com.leemyeongyun.lotto_number_machine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    //버튼 변수들 생성
    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    //각 숫자를 받아 리스트로 생성성
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.first),
            findViewById<TextView>(R.id.second),
            findViewById<TextView>(R.id.third),
            findViewById<TextView>(R.id.forth),
            findViewById<TextView>(R.id.fifth),
            findViewById<TextView>(R.id.sixth)
        )
    }

    private var didRun = false //자동생성으로 인해 번호가 가득 찰 예외 처리 변수, 변화가능이므로 var

    private val pickNumberSet = mutableSetOf<Int>() // 중복을 방지하기위한 셋

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    private fun initAddButton() {
        addButton.setOnClickListener {

            //가득 차있을 예외
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //리턴을 할때, initAddButton이 아닌 Listener를 리턴하기 위해
            }

            //5개 초과 뽑을 시 예외
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //numberPicker 값이 이미 들어있을 예외
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size]// set의 사이즈만큼 인덱스
            textView.isVisible = true // 보이지 않게 설정했으므로 다시 보이게 설정
            textView.text = numberPicker.value.toString()//picker값을 받아 string으로 넘겨줌

            pickNumberSet.add(numberPicker.value)// set에 넣음

        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()//초기화
            numberTextViewList.forEach {
                it.isVisible = false// textView를 forEach로 각각 가져와 다 안보이게 설정. for문이랑 비슷함
            }
            didRun = false
        }


    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            //이전의 forEach만 쓴다면 몇번째의 값인지 알 수 없기때문에, Indexed를 이용
            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

            }
            didRun = true

        }
    }

    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45) {
                if (pickNumberSet.contains(i)) {
                    continue // 랜덤번호를 생성할때 이미 있는 번호를 제외
                }
                this.add(i)
            }
        }
        numberList.shuffle()

        //이미 선택된 셋을 toList로 변환해주고, 나머지를 numberList에서 추가함
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        return newList.sorted()

    }
}

