package com.mcafi.calcetto

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.User
import com.mcafi.calcetto.src.MatchNotificationManager
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser
    private val userRef = db.collection("utenti").document(firebaseUser!!.uid)
    private lateinit var user: User
    val MATCH_CHANNEL_ID = "MATCH_CHANNEL"

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
        btn_notification_test.setOnClickListener(this)
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
        when (v.id) {
            R.id.btn_settings_save -> {
                user.name = et_settings_name.text.toString()
                user.username = et_settings_username.text.toString()
                userRef.update(user.toMap())
                Toast.makeText(applicationContext, "Modifiche salvate!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java).putExtra("TAB", 2))
            }
            R.id.btn_notification_test -> {
                scheduleNotification(getNotification(), 2000)
                Toast.makeText(applicationContext, "aaaaaaaaaaaaaaa", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun scheduleNotification(notification: Notification, delay: Int) {
        val notificationIntent = Intent(this, MatchNotificationManager::class.java)
        notificationIntent.putExtra("NOTIFICATION_ID", 1)
        notificationIntent.putExtra("NOTIFICATION", notification)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(): Notification {
        val builder = NotificationCompat.Builder(this, MATCH_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Titolo")
                .setContentText("Ciao ecco una notifica")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
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