package com.mcafi.calcetto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.db.DbCreator
import com.mcafi.calcetto.model.User
import kotlinx.android.synthetic.main.activity_match_view.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser
    private val userRef = db.collection("utenti").document(firebaseUser!!.uid)
    private lateinit var user: User
    private val DbSql = DbCreator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val nameText = findViewById<TextView>(R.id.et_settings_name)
        val usernameText = findViewById<TextView>(R.id.et_settings_username)
        val saveSettings = findViewById<TextView>(R.id.btn_settings_save)
        saveSettings.setOnClickListener(this)


        userRef.get().addOnSuccessListener { documentSnapshot ->
            user = documentSnapshot.toObject(User::class.java)!!
            nameText.text = user.name
            usernameText.text = user.username
        }

        val notify_user=DbSql.getUser(mAuthReg.currentUser!!.uid)
        if(notify_user==1){
            notification_switch.isChecked=true
        }
        else if(notify_user==0){
            notification_switch.isChecked=false
        }
        notification_switch.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_settings_save -> {
                user.name = et_settings_name.text.toString()
                user.username = et_settings_username.text.toString()
                userRef.update(user.toMap())
                Toast.makeText(applicationContext, "Modifiche salvate!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java).putExtra("TAB", 2))
            }
            R.id.logoutButton -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.notification_switch->{
                if(notification_switch.isChecked){
                    DbSql.execQuery("UPDATE user SET notify = 1 WHERE id_user= '"+mAuthReg.currentUser!!.uid+"';");
                    Toast.makeText(applicationContext, "Uno", Toast.LENGTH_LONG).show()
                }
                else{
                    DbSql.execQuery("UPDATE user SET notify = 0 WHERE id_user= '"+mAuthReg.currentUser!!.uid+"'  ;");
                    Toast.makeText(applicationContext, "Zero", Toast.LENGTH_LONG).show()
                }
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