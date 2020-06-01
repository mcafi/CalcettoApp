package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.Match
import kotlin.collections.ArrayList

class MainFragment : Fragment() {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, viewGroup, false)
        val addMatchButton: FloatingActionButton = view.findViewById(R.id.addMatchButton)
        addMatchButton.setOnClickListener { startActivity(Intent(activity, NewMatchActivity::class.java)) }
        listView = view.findViewById(R.id.matches_list)

        val matchList = ArrayList<Match>();

        db.collection("partite")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        matchList.add(match)
                    }
                    val adapter = MatchAdapter(context!!, matchList)
                    listView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d("Main", "Error getting documents: ", exception)
                }
        return view
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