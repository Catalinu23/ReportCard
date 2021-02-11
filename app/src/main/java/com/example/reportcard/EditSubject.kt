package com.example.reportcard

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.content_edit_subject.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

var subjectPosition: Int = -1

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

class EditSubject : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subject)
        //setSupportActionBar(findViewById(R.id.toolbar))

        // SET ACTIVITY TITLE
        subjectPosition = intent.getIntExtra(EXTRA_POSITION, -1)
        var subject = subjects.get(subjectPosition)
        val adapterGrades = ArrayAdapter (this, R.layout.list_view_layout, subject.grades)
        gradesList.adapter = adapterGrades
        subjectName.text = subject.name

        if(subject.exam && subject.examSet) {
            editExamGrade.setText(subject.examGrade.toString())
        }

        updateAverage(subject)

        addGradeButton.setOnClickListener{
            if(editNewGrade.text.isNotEmpty() && editExamGrade.text.length <= 4) {
                val grade = editNewGrade.text.toString().toInt()
                if (grade !in 1..10 || editNewGrade.text.toString().isEmpty()) {
                    Toast.makeText(this, "Introduceți o notă validă!", Toast.LENGTH_SHORT).show()
                    editNewGrade.setText("")
                } else {
                    subject.addGrade(grade)
                    subject.sumOfGrades += grade
                    editNewGrade.setText("")
                    updateAverage(subject)
                }
            }
        }

        if(subject.exam) {
            addExamGradeButton.setOnClickListener{
                if(editExamGrade.text.isNotEmpty() && editExamGrade.text.length <= 4) {
                    var examGrade = editExamGrade.text.toString().toInt()
                    if (examGrade !in 1..10 || editExamGrade.text.toString().isEmpty()) {
                        Toast.makeText(this, "Introduceți o notă validă!", Toast.LENGTH_SHORT).show()
                    } else {
                        subject.examSet = true
                        subject.examGrade = examGrade
                        updateAverage(subject)
                    }
                }
            }
        }

        gradesList.setOnItemLongClickListener{ parent, view, position, id ->

            val view = View.inflate(this, R.layout.alert_dialog_grades, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val alertDialog =builder.create()
            alertDialog.setView(view)
            //alertDialog.message.text = "Confirmați ștergerea notei?"
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            alertDialog.show()

            view.NegativeButton.setOnClickListener {
                alertDialog.dismiss()
            }

            view.PositiveButton.setOnClickListener {
                subject.sumOfGrades -= subject.grades[position]
                subject.grades.removeAt(position)
                updateAverage(subject)
                val activityIntent = Intent(this, EditSubject::class.java)
                activityIntent.putExtra(EXTRA_POSITION, subjectPosition)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(activityIntent)
            }
            return@setOnItemLongClickListener true
        }

        if(subject.exam)
            makeVisible()
        else
            makeInvisible()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val activityIntent = Intent(this, MainActivity::class.java)
        //activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(activityIntent)
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    private fun makeVisible(): Unit{
        textView3.visibility = View.VISIBLE
        addExamGradeButton.visibility = View.VISIBLE
        editExamGrade.visibility = View.VISIBLE
    }

    private fun makeInvisible(): Unit{
        textView3.visibility = View.GONE
        addExamGradeButton.visibility = View.GONE
        editExamGrade.visibility = View.GONE
    }

    private fun updateAverage(subject: Subject) {
        // GET AVERAGE GRADE
        if(subject.getNrOfGrades() == 0){
            averageGardeText.text = "Media -"
        } else {
            if(!subject.exam || !subject.examSet) {
                var avgGrade = (subject.sumOfGrades.toDouble() / subject.getNrOfGrades().toDouble()).round(2)
                averageGardeText.text = "Media $avgGrade"
            } else {
                var avgGrade = (subject.sumOfGrades.toDouble() / subject.getNrOfGrades().toDouble()).round(2)
                var avgGradeFinal = ( (avgGrade*3 + subject.examGrade) / 4 ).toDouble().round(2)
                averageGardeText.text = "Media $avgGradeFinal"
            }
        }
    }

    fun saveData(): Unit {
        val sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(subjects)
        editor.putString("task list", json)
        editor.apply()
    }

}