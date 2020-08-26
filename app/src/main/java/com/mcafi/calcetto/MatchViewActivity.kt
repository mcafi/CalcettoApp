package com.mcafi.calcetto

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
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

class MatchViewActivity : AppCompatActivity(), View.OnClickListener {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private lateinit var immagini: StorageReference
    private val mAuthReg = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser
    private var Partecipa: Boolean = false
    private var partita: Match? = null
    private lateinit var Match :DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_view)
        var IdMAtch=intent.getStringExtra("MATCH_ID");
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
        Match = db.collection("partite").document(IdMAtch)
        Match.get().addOnSuccessListener { document ->
            if (document != null) {
                //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                //println("dati: ${document.data}")
                partita = document.toObject(com.mcafi.calcetto.model.Match::class.java)

                if (partita != null) {
                    partita!!.participants= document.get("partecipants") as MutableList<String>;
                    //println("dati: ${partita.matchDate}")
                    DateTimeMatchView.text= partita!!.matchDate.toString()
                    PlaceMatchView.text= partita!!.place.address
                    nameMatchView.text= partita!!.NameMatch
                    MatchPartecipanti.text= partita!!.participants.size.toString()+"/"+ partita!!.available.toString()


                    //println("IndexOF: ${firebaseUser.uid}"+" Match: $IdMAtch")
                    //println("IndexOF: ${}")
                    if(partita!!.participants.indexOf(firebaseUser.uid)<0){
                        PartecipaLasciaButtonMatchView.text="Partecipa"
                        Partecipa=false
                    }
                    else{
                        PartecipaLasciaButtonMatchView.text="Lascia Partita"
                        Partecipa=true
                    }
                    if(partita!!.creator==firebaseUser.uid){
                        DeleteMatchView.visibility=View.VISIBLE;
                    }
                }
            }
        }
        immagini = storageRef.child("immagini_match/" + IdMAtch)
        immagini.getBytes(MAX_SIZE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ImageMatchView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            // Handle any errors
        }

        PartecipaLasciaButtonMatchView.setOnClickListener(this)
        DeleteMatchView.setOnClickListener(this)



    }

    override fun onClick(v: View) {
        println("IndexOF: $Partecipa")
        when (v.id) {
            R.id.PartecipaLasciaButtonMatchView -> {
                if(Partecipa){
                    partita!!.participants.remove(firebaseUser.uid)
                    Match.update("partecipants",partita!!.participants)
                    recreate()
                }
                else{
                    partita!!.participants.add(firebaseUser.uid)
                    Match.update("partecipants",partita!!.participants)
                    recreate()
                }
            }
            R.id.DeleteMatchView ->{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Conferma Eliminazione")
                builder.setMessage("Vuoi eliminare questa partita? ")
                builder.setPositiveButton("SI") { dialogInterface: DialogInterface, i: Int ->
                    Match.delete()
                    val viewMatchIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(viewMatchIntent)
                }
                builder.setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
                }
                builder.setNeutralButton("ANNULLA"){ dialogInterface: DialogInterface, i: Int -> }
                builder.show()
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val PICK_IMAGE = 105
        private const val MAX_SIZE = 3 * 1024 * 1024.toLong()
    }
}