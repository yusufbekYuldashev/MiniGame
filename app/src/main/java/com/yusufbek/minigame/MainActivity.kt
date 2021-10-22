package com.yusufbek.minigame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.yusufbek.minigame.databinding.ActivityMainBinding
import java.util.*
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var layoutBinding: ActivityMainBinding

    private lateinit var currentQuestion: Question
    private var typedAnswer: StringBuilder = StringBuilder()
    private var currentAmountOfMoney = STARTING_AMOUNT
    private var questionCounter = 0
    private var questionArray = arrayListOf<Question>()

    private var isHelpButtonClicked = false
    private var isClickable = false

    companion object {
        const val STARTING_AMOUNT = 200
        const val FOUND_TRUE_ANSWER = 10
        const val OPEN_LETTER = -30
        const val DELETE_LETTER = -5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = layoutBinding.root
        setContentView(view)

        setQuestions()

        setCurrentQuestion()
        currentAmountOfMoney = STARTING_AMOUNT
        updateMoney(currentAmountOfMoney)

        layoutBinding.questionTextViewContainer.children.forEachIndexed { _, questionTextView ->
            questionTextView.setOnClickListener {
                questionTextView.visibility = View.INVISIBLE
                val clickedLetter = (questionTextView as TextView).text.toString()
                val view1 = getFreeAnswerButton()
                if (view1 != null) {
                    (view1 as TextView).text = clickedLetter
                    view1.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.shape_for_text,
                        null
                    )
                    typedAnswer.clear()
                    layoutBinding.answerTextViewContainer.forEach {
                        typedAnswer.append((it as TextView).text.toString())
                    }
                }
                if (typedAnswer.length == currentQuestion.answer.length) {
                    checkAnswer()
                }
            }
        }

        layoutBinding.answerTextViewContainer.children.forEach {
            it.setOnClickListener { answerTextView ->
                if (isHelpButtonClicked) {
                    if (currentAmountOfMoney >= -OPEN_LETTER) {
                        currentAmountOfMoney += OPEN_LETTER
                        updateMoney(currentAmountOfMoney)
                    }
                    val selectedChar =
                        currentQuestion.answer[answerTextView.tag.toString().toInt()]
                    (answerTextView as TextView).text = selectedChar.toString()
                    answerTextView.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.shape_for_text,
                        null
                    )
                    answerTextView.isClickable = false

                    isHelpButtonClicked = false

                    typedAnswer.clear()
                    layoutBinding.answerTextViewContainer.forEach { textView ->
                        typedAnswer.append((textView as TextView).text)
                    }
                    hello@ for (questionText in layoutBinding.questionTextViewContainer.children) {
                        if ((questionText as TextView).text.toString() == selectedChar.toString() && it.visibility == View.VISIBLE) {
                            questionText.text = ""
                            questionText.isClickable = false
                            break@hello
                        }
                    }
                    if (typedAnswer.length == currentQuestion.answer.length) {
                        checkAnswer()
                    }

                } else {
                    val text = (answerTextView as TextView).text.toString()
                    getRightButton(text)
                    answerTextView.text = ""
                    answerTextView.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.shape,
                        null
                    )
                    typedAnswer.clear()
                    layoutBinding.answerTextViewContainer.children.forEach { answerText ->
                        typedAnswer.append((answerText as TextView).text.toString())
                    }
                }
            }
        }


        layoutBinding.openLetter.setOnClickListener {
            if (currentAmountOfMoney >= -OPEN_LETTER) {
                isHelpButtonClicked = true
            } else {
                Toast.makeText(this, "You do not have enough money", Toast.LENGTH_SHORT).show()
            }
        }

        layoutBinding.deleteLetter.setOnClickListener {
            if (currentAmountOfMoney >= -DELETE_LETTER) {
                layoutBinding.questionTextViewContainer.forEach { it1 ->
                    val currentLetter = (it1 as TextView).text.toString()
                    if (!currentQuestion.answer.contains(currentLetter) && it1.visibility == View.VISIBLE) {
                        it1.visibility = View.INVISIBLE
                        currentAmountOfMoney += DELETE_LETTER
                        updateMoney(currentAmountOfMoney)
                        return@setOnClickListener
                    }
                }
            } else {
                Toast.makeText(this, "You do not have enough money", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAnswer() {
        if (typedAnswer.toString() == currentQuestion.answer) {
            Toast.makeText(this@MainActivity, "True", Toast.LENGTH_SHORT).show()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    currentAmountOfMoney += FOUND_TRUE_ANSWER
                    runOnUiThread {
                        updateMoney(currentAmountOfMoney)
                        if (questionCounter == questionArray.size - 1) {
                            AlertDialog.Builder(this@MainActivity)
                                .setMessage("You have won the game!!! Congratulations!!!")
                                .setPositiveButton(
                                    "Finish"
                                ) { _, _ -> finish() }.setNegativeButton("Restart") { _, _ ->
                                    questionCounter = 0
                                    setCurrentQuestion()
                                    currentAmountOfMoney = STARTING_AMOUNT
                                    updateMoney(currentAmountOfMoney)
                                }.show()
                        } else {
                            questionCounter++
                            setCurrentQuestion()
                        }
                    }
                }
            }, 500)
        } else {
            Toast.makeText(this, "False", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMoney(newAmount: Int) {
        layoutBinding.moneyCounterTextView.text = newAmount.toString()
    }

    private fun getFreeAnswerButton(): View? {
        layoutBinding.answerTextViewContainer.children.forEach {
            if ((it as TextView).text.toString().isEmpty()) {
                return it
            }
        }
        return null
    }

    private fun getRightButton(text: String) {

        if (!isClickable) {
            layoutBinding.questionTextViewContainer.children.forEach {
                if ((it as TextView).text != "") {
                    (it).isClickable = true
                }
            }
            isClickable = true
        }

        layoutBinding.questionTextViewContainer.children.forEach {
            if ((it as TextView).text.toString() == text && it.visibility == View.INVISIBLE) {
                it.visibility = View.VISIBLE
                return
            }
        }
    }

    private fun setQuestions() {
        val question1 = Question(
            1,
            R.drawable.tree1,
            R.drawable.tree2,
            R.drawable.tree3,
            R.drawable.tree4,
            "tree",
            "abtnmreccioe"
        )
        val question2 = Question(
            2,
            R.drawable.heat1,
            R.drawable.heat2,
            R.drawable.heat3,
            R.drawable.heat4,
            "heat",
            "eyhterlaiuit"
        )
        val question3 = Question(
            3,
            R.drawable.house1,
            R.drawable.house2,
            R.drawable.house3,
            R.drawable.house4,
            "house",
            "aahoiutslkle"
        )
        val question4 = Question(
            4,
            R.drawable.color1,
            R.drawable.color2,
            R.drawable.color3,
            R.drawable.color4,
            "color",
            "capilopleofr"
        )
        val question5 = Question(
            5,
            R.drawable.ball1,
            R.drawable.ball2,
            R.drawable.ball3,
            R.drawable.ball4,
            "ball",
            "abcelaiolpel"
        )
        questionArray.addAll(listOf(question1, question2, question3, question4, question5))
    }

    private fun setCurrentQuestion() {
        typedAnswer.clear()
        currentQuestion = questionArray[questionCounter]

        layoutBinding.counterText.text = (questionCounter + 1).toString()
        layoutBinding.imageView1.setImageResource(currentQuestion.img1)
        layoutBinding.imageView2.setImageResource(currentQuestion.img2)
        layoutBinding.imageView3.setImageResource(currentQuestion.img3)
        layoutBinding.imageView4.setImageResource(currentQuestion.img4)

        layoutBinding.questionTextViewContainer.forEachIndexed { index, view ->
            view.visibility = View.VISIBLE
            view.isClickable = true
            (view as TextView).text = currentQuestion.question[index].toString()
            isClickable = true
        }

        layoutBinding.answerTextViewContainer.children.forEachIndexed { index, answerTextView ->
            (answerTextView as TextView).text = ""
            if (index >= currentQuestion.answer.length) {
                answerTextView.visibility = View.GONE
            } else {
                answerTextView.visibility = View.VISIBLE
                answerTextView.isClickable = true
                answerTextView.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.shape,
                    null
                )
            }
        }

    }
}