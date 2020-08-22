package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mcafi.calcetto.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        setContentView(R.layout.activity_login)
        btn_login_login.setOnClickListener(this)
        btn_login_signup.setOnClickListener(this)
        btn_login_google.setOnClickListener(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signIn:success", "e")
                        val firebaseUser = mAuth.currentUser
                        val userRef = db.collection("utenti").document(firebaseUser!!.uid)
                        userRef.get().addOnCompleteListener { completeTask ->
                            if (task.isSuccessful) {
                                val document = completeTask.result!!
                                if (document.exists()) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                } else {
                                    val user = User(firebaseUser.email, firebaseUser.displayName, "bomber")
                                    db.collection("utenti").document(firebaseUser.uid).set(user)
                                }
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signIn:failure", task.exception)
                        startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    }
                }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login_login -> {
                val email = et_login_email.text.toString()
                val password = et_login_password.text.toString()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "createUserWithEmail:success")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btn_login_google -> {
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.btn_login_signup -> {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }
        }
    }
}