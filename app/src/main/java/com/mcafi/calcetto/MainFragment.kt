package com.mcafi.calcetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.mcafi.calcetto.model.Match
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : Fragment() {
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, viewGroup, false)
        val addMatchButton: FloatingActionButton = view.findViewById(R.id.addMatchButton)
        var ButtonSearchClicked =false

        addMatchButton.setOnClickListener { startActivity(Intent(activity, NewMatchActivity::class.java)) }
        listView = view.findViewById(R.id.matches_list)

        val matchList = ArrayList<Match>()

        db.collection("partite")
                .whereGreaterThan("matchDate", Calendar.getInstance().timeInMillis)
                .orderBy("matchDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        match.id = document.id
                        matchList.add(match)
                    }
                    val adapter = MatchAdapter(context!!, matchList)
                    listView.adapter = adapter
                    view.findViewById<ProgressBar>(R.id.progressBarMatch).visibility = View.INVISIBLE;
                }
                .addOnFailureListener { exception ->
                    Log.d("Main", "Error getting documents: ", exception)
                }

        listView.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
            val match = parent.getItemAtPosition(position) as Match
            val viewMatchIntent = Intent(activity, MatchViewActivity::class.java)
            viewMatchIntent.putExtra("MATCH_ID", match.id)
            startActivity(viewMatchIntent)
        }

        var ButtonSearch : Button = view.findViewById(R.id.SearchButton)
        ButtonSearch.setOnClickListener {
            val NomeMatch = NameMatchSearch.text.toString()
            val Citta = CittaSearch.text.toString()
            SearchMatch(Citta,NomeMatch, view)
        }

        var SearchIco : ImageView = view.findViewById(R.id.SearchIco)
        SearchIco.setOnClickListener {
            val LayoutS: LinearLayout = view.findViewById(R.id.LayoutSearch)
            if (!ButtonSearchClicked) {
                val params: ViewGroup.LayoutParams = LayoutS.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = 150
                LayoutS.layoutParams = params
                ButtonSearchClicked = true
                SearchMatch(NameMatchSearch.text.toString(), CittaSearch.text.toString(), view)
            } else {
                val params: ViewGroup.LayoutParams = LayoutS.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = 1
                LayoutS.layoutParams = params
                ButtonSearchClicked = false
                SearchMatch("", "", view)
            }
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

    private fun SearchMatch(Citta:String,NomeMatch:String,view:View){
        val matchList = ArrayList<Match>()

        db.collection("partite")
                .whereGreaterThan("matchDate", Calendar.getInstance().timeInMillis)
                .orderBy("matchDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        match.id = document.id
                        if(match.place.address.toLowerCase().contains(Citta.toLowerCase()) && match.matchName.toLowerCase().contains(NomeMatch.toLowerCase())) {
                            Log.v("Search","Trovato")
                            matchList.add(match)
                        }
                    }
                    val adapter = MatchAdapter(context!!, matchList)
                    listView.adapter = adapter
                    view.findViewById<ProgressBar>(R.id.progressBarMatch).visibility = View.INVISIBLE;
                }
                .addOnFailureListener { exception ->
                    Log.d("Main", "Error getting documents: ", exception)
                }

        listView.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
            val match = parent.getItemAtPosition(position) as Match
            val viewMatchIntent = Intent(activity, MatchViewActivity::class.java)
            viewMatchIntent.putExtra("MATCH_ID", match.id)
            startActivity(viewMatchIntent)
        }
    }
}