package com.mcafi.calcetto

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mcafi.calcetto.model.Match
import com.mcafi.calcetto.model.User
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var v: View
    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private lateinit var firebaseUser: FirebaseUser
    private val storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private lateinit var profileIcon: ImageView
    private val storageRef = storage.reference
    private lateinit var immagini: StorageReference
    private lateinit var listView: ListView
    private lateinit var listView2: ListView

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_profile, viewGroup, false)
        firebaseUser = mAuthReg.currentUser!!
        immagini = storageRef.child("immagini_profilo/" + firebaseUser.uid)

        val userRef = db.collection("utenti").document(firebaseUser.uid)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            writeUserData(documentSnapshot.toObject(User::class.java)!!)

        }
        userRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                writeUserData(snapshot.toObject(User::class.java)!!)
            }
        }
        v.findViewById<Button>(R.id.logoutButton).setOnClickListener(this)
        v.findViewById<Button>(R.id.editPicturebutton).setOnClickListener(this)
        val settingsButton: FloatingActionButton = v.findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener(this)

        profileIcon = v.findViewById(R.id.profileIcon)
        immagini.getBytes(MAX_SIZE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profileIcon.setImageBitmap(bitmap)
        }.addOnFailureListener {
            // Handle any errors
        }



        listView = v.findViewById(R.id.matches_list)


        val matchList = ArrayList<Match>();
        val matchList2 = ArrayList<Match>();

        db.collection("partite")
                .orderBy("matchDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        match.id = document.id
                        if (document.get("partecipants") != null) {
                            match.participants = document.get("partecipants") as ArrayList<String>
                        } else {
                            match.participants = ArrayList()
                        }
                        if(match.creator == firebaseUser.uid || match.participants.contains(firebaseUser.uid))
                        matchList.add(match)



                    }

                    val adapter = MatchAdapterProfile(context!!, matchList)

                    listView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d("Main", "Error getting documents: ", exception)
                }

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val match = parent.getItemAtPosition(position) as Match
            val viewMatchIntent = Intent(activity, MatchViewActivity::class.java)
            viewMatchIntent.putExtra("MATCH_ID", match.id)
            startActivity(viewMatchIntent)
        }


        return v
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.logoutButton -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
            }
            R.id.settingsButton -> startActivity(Intent(activity, SettingsActivity::class.java))
            R.id.editPicturebutton -> {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, PICK_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            profileIcon.setImageURI(imageUri)
            val uploadTask = immagini.putFile(imageUri!!)
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener { // Handle unsuccessful uploads
                Toast.makeText(context, "Errore durante l'upload dell'immagine", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(context, "Immagine del profilo modificata", Toast.LENGTH_LONG).show()
            }
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

    // inserisce i dati dell'utente nei rispettivi campi di testo
    private fun writeUserData(user: User) {
        nameText.text = user.name
        usernameText.text = user.username
        emailText.text = user.email
    }

    companion object {
        private const val PICK_IMAGE = 100
        private const val MAX_SIZE = 3 * 1024 * 1024.toLong()
    }
}