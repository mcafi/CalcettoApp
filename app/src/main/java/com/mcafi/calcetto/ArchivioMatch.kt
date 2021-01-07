package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.mcafi.calcetto.model.Match
import kotlinx.android.synthetic.main.archivio_match.*
import java.util.*
import kotlin.collections.ArrayList

class ArchivioMatch : AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.archivio_match)
        setSupportActionBar(archivioToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        LinkHome.setOnClickListener(this)

        firebaseUser = mAuthReg.currentUser!!
        listView = findViewById(R.id.list_match)
        val matchList = ArrayList<Match>();

        db.collection("partite")
                .orderBy("matchDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        match.id = document.id
                        if (document.get("partecipants") != null) {
                            match.partecipants = document.get("partecipants") as ArrayList<String>
                        } else {
                            match.partecipants = ArrayList()
                        }
                        if(match.creator == firebaseUser.uid || match.partecipants.contains(firebaseUser.uid))
                            matchList.add(match)
                    }

                    val adapter = MatchAdapterProfile(this, matchList)

                    listView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d("Main", "Error getting documents: ", exception)
                }

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val match = parent.getItemAtPosition(position) as Match
            val viewMatchIntent = Intent(this, MatchViewActivity::class.java)
            viewMatchIntent.putExtra("MATCH_ID", match.id)
            startActivity(viewMatchIntent)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.LinkHome -> startActivity(Intent(this, MainActivity::class.java))
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