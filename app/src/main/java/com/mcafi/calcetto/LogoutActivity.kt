package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LogoutActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        val logout = findViewById<Button>(R.id.logoutButton)
        logout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@LogoutActivity, LoginActivity::class.java))
    }
}