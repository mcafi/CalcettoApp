package com.mcafi.calcetto

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mcafi.calcetto.model.DateTime
import com.mcafi.calcetto.model.Match
import com.mcafi.calcetto.model.MatchPlace
import kotlinx.android.synthetic.main.activity_new_match.*
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*

class NewMatchActivity : AppCompatActivity(), View.OnClickListener {

    private val mAuthReg = FirebaseAuth.getInstance()
    private val db = initializeDatabase()
    private val firebaseUser = mAuthReg.currentUser!!
    private lateinit var dpd: DatePickerDialog
    private lateinit var tpd: TimePickerDialog

    private val c: Calendar = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val hour = c.get(Calendar.HOUR_OF_DAY)
    private val minute = c.get(Calendar.MINUTE)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("it"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("it"))
    private var matchPlace: MatchPlace = MatchPlace()

    private val storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private lateinit var ImageMatchCopertina: ImageView
    private val storageRef = storage.reference
    private lateinit var immagini: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_match)

        Places.initialize(this@NewMatchActivity, "AIzaSyDcEEGrjxw712L4SX0oUWQ0Cgn3BKPAbGE")
        //val placesClient = Places.createClient(this)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        newMatchDate.setText(dateFormat.format(c.time))
        newMatchTime.setText(timeFormat.format(c.time))

        dpd = DatePickerDialog(this@NewMatchActivity, R.style.DialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            c.set(year, monthOfYear, dayOfMonth)
            newMatchDate.setText(dateFormat.format(c.time))
        }, year, month, day)
        tpd = TimePickerDialog(this@NewMatchActivity, R.style.DialogTheme, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
            c.set(Calendar.MINUTE, minute)
            newMatchTime.setText(timeFormat.format(c.time))
        }, hour, minute, true)

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                matchPlace = MatchPlace(place.id!!, place.latLng!!.latitude, place.latLng!!.longitude, place.name!!, place.address!!)
                Log.i("TAG", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(p0: Status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: $p0")
            }
        })

        newMatchDate.setOnClickListener(this)
        newMatchTime.setOnClickListener(this)
        saveMatchButton.setOnClickListener(this)


        ImageMatchCopertina = findViewById(R.id.ImageMatchCopertina)
        ChangeImageMatch.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.newMatchDate -> {
                dpd.show()
            }
            R.id.newMatchTime -> {
                tpd.show()
            }
            R.id.saveMatchButton -> {
                val list : MutableList<String> = ArrayList<String>()
                val match = Match(firebaseUser.uid, DateTime(Calendar.getInstance()), DateTime(c), newMatchNotes.text.toString(), list, parseInt(availableSpots.text.toString()), matchPlace!!, nameMatch.text.toString())
                db.collection("partite").add(match).addOnSuccessListener { documentReference ->
                    //Toast.makeText(applicationContext, "id is ${documentReference.id}", Toast.LENGTH_LONG).show()
                    if(imageUri!=null){
                        immagini = storageRef.child("immagini_match/" + documentReference.id)
                        val uploadTask = immagini.putFile(imageUri!!)
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener { // Handle unsuccessful uploads
                            Toast.makeText(applicationContext, "Errore durante l'upload dell'immagine", Toast.LENGTH_LONG).show()
                        }.addOnSuccessListener {
                            val viewMatchIntent = Intent(applicationContext, MainActivity::class.java)
                            viewMatchIntent.putExtra("MATCH_ID", documentReference.id)
                            startActivity(viewMatchIntent)
                        }
                    }
                    else{
                        val viewMatchIntent = Intent(applicationContext, MainActivity::class.java)
                        viewMatchIntent.putExtra("MATCH_ID", documentReference.id)
                        startActivity(viewMatchIntent)
                        //println("ERRORE")
                    }
                }
                        .addOnFailureListener {documentReference ->
                            println("ERRORE")
                        }

            }
            R.id.ChangeImageMatch -> {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, NewMatchActivity.PICK_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == NewMatchActivity.PICK_IMAGE) {
            imageUri = data?.data
            ImageMatchCopertina.setImageURI(imageUri)
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


    companion object {
        private const val PICK_IMAGE = 200
        private const val MAX_SIZE = 3 * 1024 * 1024.toLong()
    }
}