package com.mcafi.calcetto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    /*
    Questa activity serve solo a verificare se l'utente è già loggato, mandandolo quindi alla schermata
    di login o alla schermata principale dell'app.
     */
    public override fun onStart() {
        val currentUser = mAuth.currentUser
        startActivity(Intent(this@StartupActivity, if (currentUser == null) LoginActivity::class.java else MainActivity::class.java))
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }
}