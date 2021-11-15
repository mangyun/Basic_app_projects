package com.leemyeongyun.calculator

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private var isOperator = false // 연산자 입력시
    private var hasOperator = false // 연산자가 이미 있을시시
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //인자를 view로 받아, 각 상황에서 클릭함수 구현
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("×")
            R.id.buttonDivider -> operatorButtonClicked("÷")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    private fun numberButtonClicked(number: String) {

        if (isOperator) {//연산자를 입력한경우 띄어쓰기
            expressionTextView.append(" ")
        }

        isOperator = false

        //예외처리
        val expressionText = expressionTextView.text.split(" ") //split함수를 통해 공백으로 띄어, 배열에 저장함
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {//숫자가 비어있지 않고, 지금 입력하는 숫자의 개수가 15개를넘어가는지 측정
            Toast.makeText(this, "15자리까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {//입력숫자가 비어있고, 0인 경우
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        expressionTextView.append(number)
        resultTextView.text = calculateExpression()
    }


    private fun operatorButtonClicked(operator: String) {
        //예외처리
        if (expressionTextView.text.isEmpty()) {//비어있다면
            return
        }
        when {
            isOperator -> {
                val text = expressionTextView.text.toString()// 연산자 다시 입력시 연산자 교체
                expressionTextView.text = text.dropLast(1) + operator//dropLast는 n만큼 문자를 버리는 함수
            }

            hasOperator -> {//연산자는 연속해서 사용 불가능
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        //범위를 지정해 색칠, 여기서는 연산자만 초록색으로 색칠
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(//단독 getColor가 deprecated되면서 ContextCompat의 getColor를 이용
                    this,
                    R.color.green
                )
            ),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb

        isOperator = true
        hasOperator = true


    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        if ( expressionTextView.text.isEmpty() || expressionTexts.size == 1){ //비어있거나, 숫자가 1개인 경우
            Toast.makeText(this, "수식을 완성해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if(expressionTexts.size != 3 && hasOperator){
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        //미관상 값을 expressionTextview쪽으로 이동시켜 출력
        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false



    }

    //값 계산 함수
    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "×" -> (exp1 * exp2).toString()
            "÷" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    //초기화
    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View) {

    }


}

//String의 확장함수형식으로 숫자변환함수 생성
fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger() //BigInteger는 무한대까지 저장가능
        true
    } catch (e: NumberFormatException) { //숫자로 제대로 치환이 안될경우
        false
    }
}