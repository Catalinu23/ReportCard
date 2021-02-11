package com.example.reportcard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

//import com.example.reportcard.DataManager as DataManager

// banner ad id: ca-app-pub-8115625286466974/4318655476
// app id: ca-app-pub-8115625286466974~8460246312

// google banner ad id: ca-app-pub-3940256099942544/6300978111
// google app id: ca-app-pub-3940256099942544~3347511713

val EXTRA_POSITION = "EXTRA_POSITION"
var subjects = mutableListOf<Subject>();
var appOpen = false
var listExists = false

const val SHARED_PREFS = "sharedPrefs"
const val TEXT = "text"

class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadAppState()
        //Toast.makeText(this, listExists.toString(), Toast.LENGTH_SHORT).show()
        if(!appOpen) {
            if (listExists) {
                loadData()
                //Toast.makeText(this, "The app is open", Toast.LENGTH_SHORT).show()
            }
            appOpen = true
        }
        listExists = true
        listExists = true
        saveAppState()
        MobileAds.initialize(this) {}
        /*val testDeviceIds = Arrays.asList("E636E04C9142614C476A5F6F6318CCE4")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)*/
        val adRequest = AdRequest.Builder().build()
        //adView.adSize = AdSize.BANNER
        //adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.loadAd(adRequest)
        //adView.loadAd(AdRequest.Builder().build())

        listSubjects.adapter = ArrayAdapter(this, R.layout.list_view_layout, subjects)

        addButton1.setOnClickListener{
            val addIntent = Intent(this, AddSubject::class.java)
            addIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(addIntent)
        }

        listSubjects.setOnItemClickListener{parent, view, position, id ->
            val editIntent = Intent(this, EditSubject::class.java)
            editIntent.putExtra(EXTRA_POSITION, position)
            editIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(editIntent)
        }

        listSubjects.setOnItemLongClickListener{parent, view, position, id ->
            val view = View.inflate(this, R.layout.alert_dialog, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val alertDialog =builder.create()
            alertDialog.setView(view)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            alertDialog.show()

            view.NegativeButton.setOnClickListener {
                alertDialog.dismiss()
            }

            view.PositiveButton.setOnClickListener {
                subjects.removeAt(position)
                val activityIntent = Intent(this, MainActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(activityIntent)
                //alertDialog.dismiss()
            }
            return@setOnItemLongClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        saveData()
    }*/

    override fun onPause() {
        super.onPause()
        saveData()
    }

    fun saveData(): Unit {
        val sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(subjects)
        editor.putString("task list", json)
        editor.apply()
    }

    fun loadData(): Unit {
        val sharedPreferences = getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("task list", null)
        val type: Type = object: TypeToken<ArrayList<Subject>>() {}.type
        subjects = gson.fromJson(json, type)
        if(subjects == null)
            subjects = emptyList<Subject>() as MutableList<Subject>
    }

    fun saveAppState() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean(TEXT, listExists)
        editor.apply()
    }

    fun loadAppState() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        listExists = sharedPreferences.getBoolean(TEXT, false)
    }
}
fun getNrOfSubjects(): Int {
    return subjects.size;
}

fun addSubject(name: String, exam: Boolean): Unit {
    subjects.add(Subject(name, exam));
}




