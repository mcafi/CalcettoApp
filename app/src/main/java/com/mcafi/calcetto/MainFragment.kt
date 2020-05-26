package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, viewGroup, false)
        val addMatchButton: FloatingActionButton = view.findViewById(R.id.addMatchButton)
        addMatchButton.setOnClickListener { startActivity(Intent(activity, NewMatchActivity::class.java)) }
        return view
    }
}