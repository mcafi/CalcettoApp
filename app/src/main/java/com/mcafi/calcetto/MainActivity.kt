package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.mcafi.calcetto.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener {

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
        SettingLink.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.SettingLink -> startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
