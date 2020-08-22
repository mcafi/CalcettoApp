package com.mcafi.calcetto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class StartupActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    public override fun onStart() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@StartupActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@StartupActivity, LoginActivity::class.java))
        }
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }
}