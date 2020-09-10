package com.mcafi.calcetto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mcafi.calcetto.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        sectionsPagerAdapter.addFragment(MapFragment(), "Mappa")
        sectionsPagerAdapter.addFragment(MainFragment(), "Partite")
        sectionsPagerAdapter.addFragment(ProfileFragment(), "Profilo")

        //carica di default il tab centrale, o un altro tab se esplicitamente settato
        viewPager.currentItem = intent.getIntExtra("TAB", 1)


        if(intent.getStringExtra("MATCH_ID") != null) {
            val viewMatchIntent = Intent(applicationContext, MatchViewActivity::class.java)
            viewMatchIntent.putExtra("MATCH_ID", intent.getStringExtra("MATCH_ID"))
            startActivity(viewMatchIntent)
        }

        val builder = NotificationCompat.Builder(this, "CANALE_PROVA")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Titolo")
                .setContentText("Ciao ecco una notifica")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("CANALE_PROVA", "Canale prova", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "descrizione canale" }
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(3, builder.build())
        }
    }

}