package com.example.reportcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_add_subject.*

class AddSubject : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)
        //setSupportActionBar(findViewById(R.id.toolbar))

        addButton1.setOnClickListener {
            if(editSubjectName.text.isNotEmpty()) {
                val exam = checkBox.isChecked
                val subjectName = editSubjectName.text.toString()
                addSubject(subjectName, exam)
                val activityIntent = Intent(this, MainActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(activityIntent)
            }
        }
    }
}