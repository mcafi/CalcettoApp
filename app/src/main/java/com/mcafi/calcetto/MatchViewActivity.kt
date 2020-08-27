package com.mcafi.calcetto

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mcafi.calcetto.model.Match
import kotlinx.android.synthetic.main.activity_match_view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MatchViewActivity : AppCompatActivity(), View.OnClickListener {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private lateinit var immagini: StorageReference
    private val mAuthReg = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser
    private var partecipa: Boolean = false
    private lateinit var partita: Match
    private lateinit var matchReference: DocumentReference
    private val dateTimeFormat = SimpleDateFormat("dd/MMM/yyyy - HH:mm", Locale("it"))
    private val dateTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_view)
        val matchId = intent.getStringExtra("MATCH_ID");
        //Toast.makeText(baseContext, IdMAtch, Toast.LENGTH_LONG).show()
        val toolbar = findViewById<View>(R.id.matchToolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        firebaseUser = mAuthReg.currentUser!!

        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
        matchReference = db.collection("partite").document(matchId!!)
        matchReference.get().addOnSuccessListener { document ->
            if (document != null) {
                //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                //println("dati: ${document.data}")
                partita = document.toObject(Match::class.java)!!

                if (document.get("partecipants") != null) {
                    partita.participants = document.get("partecipants") as ArrayList<String>
                } else {
                    partita.participants = ArrayList()
                }

                dateTime.timeInMillis = partita.matchDate

                //println("dati: ${partita.matchDate}")
                tv_match_view_datetime.text = dateTimeFormat.format(dateTime.time)
                tv_match_view_place.text = partita.place.address
                tv_match_view_name.text = partita.matchName
                tv_match_view_partecipants.text = "${partita.participants.size.toString()}/${partita.available.toString()}"


                //println("IndexOF: ${firebaseUser.uid}"+" Match: $IdMAtch")
                //println("IndexOF: ${}")
                if (partita.participants.indexOf(firebaseUser.uid) < 0) {
                    btn_match_view_partecipate_leave.text = "Partecipa"
                    partecipa = false
                }
                else {
                    btn_match_view_partecipate_leave.text = "Lascia Partita"
                    partecipa = true
                }
                if (partita.creator == firebaseUser.uid){
                    btn_match_view_delete.visibility = View.VISIBLE;
                }
            }
        }
        immagini = storageRef.child("immagini_match/$matchId")
        immagini.getBytes(MAX_SIZE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            img_match_view.setImageBitmap(bitmap)
        }.addOnFailureListener {
            // Handle any errors
        }

        btn_match_view_partecipate_leave.setOnClickListener(this)
        btn_match_view_delete.setOnClickListener(this)



    }

    override fun onClick(v: View) {
        println("IndexOF: $partecipa")
        when (v.id) {
            R.id.btn_match_view_partecipate_leave -> {
                if (partecipa) {
                    partita.participants.remove(firebaseUser.uid)
                    matchReference.update("partecipants", partita.participants)
                    recreate()
                }
                else {
                    partita.participants.add(firebaseUser.uid)
                    matchReference.update("partecipants", partita.participants)
                    recreate()
                }
            }
            R.id.btn_match_view_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Conferma Eliminazione")
                builder.setMessage("Vuoi eliminare questa partita? ")
                builder.setPositiveButton("SI") { _: DialogInterface, _: Int ->
                    matchReference.delete()
                    val viewMatchIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(viewMatchIntent)
                }
                builder.setNegativeButton("NO") { _: DialogInterface, _: Int -> }
                builder.setNeutralButton("ANNULLA") { _: DialogInterface, _: Int -> }
                builder.show()
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val MAX_SIZE = 8 * 1024 * 1024.toLong()
    }
}