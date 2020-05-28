package com.mcafi.calcetto

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.Match
import kotlinx.android.synthetic.main.activity_new_match.*
import java.util.*

class NewMatchActivity : AppCompatActivity(), View.OnClickListener {

    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser!!
    private lateinit var dpd: DatePickerDialog

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_match)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        dpd = DatePickerDialog(this@NewMatchActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
        }, year, month, day)
        newMatchDate.setOnClickListener(this)
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
            R.id.saveMatchButton -> {
                var match = Match(firebaseUser.uid, c)
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