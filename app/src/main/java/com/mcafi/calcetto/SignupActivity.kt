package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mcafi.calcetto.model.User

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_signup)
        val registrazione = findViewById<Button>(R.id.ButtonReg)
        val giaReg = findViewById<TextView>(R.id.giaRegistrato)
        registrazione.setOnClickListener(this)
        giaReg.setOnClickListener(this)
        super.onCreate(savedInstanceState)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ButtonReg -> {
                val nameReg = findViewById<EditText>(R.id.nameReg)
                val name = nameReg.text.toString()
                val usernameReg = findViewById<EditText>(R.id.usernameReg)
                val username = usernameReg.text.toString()
                val emailReg = findViewById<EditText>(R.id.emailReg)
                val email = emailReg.text.toString()
                val passwordReg = findViewById<EditText>(R.id.passwordReg)
                val password = passwordReg.text.toString()
                mAuthReg.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Reg", "createUserWithEmail:success")
                        val firebaseUser = mAuthReg.currentUser
                        val user = User(email, name, username)
                        db.collection("utenti").document(firebaseUser!!.uid).set(user)
                        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Reg", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.giaRegistrato -> {
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }
        }
    }
}