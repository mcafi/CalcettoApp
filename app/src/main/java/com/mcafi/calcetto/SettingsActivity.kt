package com.mcafi.calcetto

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.User

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser
    private val userRef = db.collection("utenti").document(firebaseUser!!.uid)
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<View>(R.id.settingsToolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val nameText = findViewById<TextView>(R.id.nameText)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val saveSettings = findViewById<TextView>(R.id.saveSettings)
        saveSettings.setOnClickListener(this)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            user = documentSnapshot.toObject(User::class.java)!!
            nameText.text = user.name
            usernameText.text = user.username
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        val nameText = findViewById<TextView>(R.id.nameText)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        user.name = nameText.text.toString()
        user.username = usernameText.text.toString()
        userRef.update(user.toMap())
        Toast.makeText(applicationContext, "Modifiche salvate!", Toast.LENGTH_LONG).show()
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