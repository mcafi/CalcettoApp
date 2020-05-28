package com.mcafi.calcetto

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.Match
import kotlinx.android.synthetic.main.activity_new_match.*
import java.text.SimpleDateFormat
import java.util.*

class NewMatchActivity : AppCompatActivity(), View.OnClickListener {

    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser!!
    private lateinit var dpd: DatePickerDialog
    private lateinit var tpd: TimePickerDialog

    private val c: Calendar = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val hour = c.get(Calendar.HOUR_OF_DAY)
    private val minute = c.get(Calendar.MINUTE)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("it"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("it"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_match)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        newMatchDate.setText(dateFormat.format(c.time))
        newMatchTime.setText(timeFormat.format(c.time))

        dpd = DatePickerDialog(this@NewMatchActivity, R.style.DialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            newMatchDate.setText(dateFormat.format(c.time))
        }, year, month, day)
        tpd = TimePickerDialog(this@NewMatchActivity, R.style.DialogTheme, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
            c.set(Calendar.MINUTE, minute)
            newMatchTime.setText(timeFormat.format(c.time))
        }, hour, minute, true)

        newMatchDate.setOnClickListener(this)
        newMatchTime.setOnClickListener(this)
        saveMatchButton.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.newMatchDate -> {
                dpd.show()
            }
            R.id.newMatchTime -> {
                tpd.show()
            }
            R.id.saveMatchButton -> {
                val match = Match(firebaseUser.uid, c, newMatchNotes.text.toString())
                db.collection("partite").add(match)
            }
        }
    }

    private fun initializeDatabase(): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
        return db
    }
}