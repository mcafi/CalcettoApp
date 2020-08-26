package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mcafi.calcetto.model.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        btn_signup_signup.setOnClickListener(this)
        btn_signup_login.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_signup_signup -> {
                val name = et_signup_name.text.toString()
                val username = et_signup_username.text.toString()
                val email = et_signup_email.text.toString()
                val password = et_signup_password.text.toString()
                mAuthReg.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Reg", "createUserWithEmail:success")
                        val firebaseUser = mAuthReg.currentUser
                        val user = User(email, name, username)
                        db.collection("utenti").document(firebaseUser!!.uid).set(user)
                        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                    } else {
                        Log.w("Reg", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btn_signup_login -> {
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }
        }
    }
}