package com.mcafi.calcetto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        //carica di default il fragment centrale
        viewPager.currentItem = 1
    }
}