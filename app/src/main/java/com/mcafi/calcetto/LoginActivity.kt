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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mcafi.calcetto.LoginActivity
import com.mcafi.calcetto.model.User

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val RC_SIGN_IN = 7
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    public override fun onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        setContentView(R.layout.activity_login)
        val registrazione = findViewById<Button>(R.id.Login)
        val giaReg = findViewById<TextView>(R.id.nonRegistrato)
        registrazione.setOnClickListener(this)
        giaReg.setOnClickListener(this)
        findViewById<View>(R.id.google_login).setOnClickListener(this)
        super.onCreate(savedInstanceState)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("firebaseAuthWithGoogle:", account!!.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google sign in failed", e)
                // ...
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
                        userRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document!!.exists()) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                } else {
                                    val user = User(firebaseUser.email, "Nome e cognome", "bomber")
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
            R.id.Login -> {
                val emailLog = findViewById<EditText>(R.id.emailLog)
                val email = emailLog.text.toString()
                val passwordLog = findViewById<EditText>(R.id.passwordLog)
                val password = passwordLog.text.toString()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "createUserWithEmail:success")
                        val user = mAuth.currentUser
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Login", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
            }
            R.id.google_login -> {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.nonRegistrato -> {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }
        }
    }
}