package com.mcafi.calcetto

import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mcafi.calcetto.model.Match
import com.mcafi.calcetto.model.User
import com.mcafi.calcetto.src.MatchNotificationManager
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
    private lateinit var matchId: String
    private lateinit var matchReference: DocumentReference
    private val dateTimeFormat = SimpleDateFormat("dd/MMM/yyyy - HH:mm", Locale("it"))
    private val dateTime = Calendar.getInstance()
    private var TextPartecipanti=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_view)
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

        matchId = intent.getStringExtra("MATCH_ID")!!
        db.firestoreSettings = settings
        matchReference = db.collection("partite").document(matchId)
        matchReference.get().addOnSuccessListener { document ->
            if (document != null) {
                //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                //println("dati: ${document.data}")

                partita = document.toObject(Match::class.java)!!
                getPartecipants(db)


                dateTime.timeInMillis = partita.matchDate

                //println("dati: ${partita.matchDate}")
                tv_match_view_datetime.text = dateTimeFormat.format(dateTime.time)
                tv_match_view_place.text = partita.place.address
                tv_match_view_name.text = partita.matchName


                //println("IndexOF: ${firebaseUser.uid}"+" Match: $IdMAtch")
                //println("IndexOF: ${}")
                partecipa = partita.partecipants.contains(firebaseUser.uid)
                //Log.d("MatchView", partecipa.toString())
                btn_match_view_partecipate_leave.text = if (partecipa) "Lascia partita" else "Partecipa"

                if (partita.creator == firebaseUser.uid){
                    btn_match_view_delete.visibility = View.VISIBLE;
                    btn_match_view_partecipate_leave.visibility = View.INVISIBLE;
                }
            }
        }


        immagini = storageRef.child("immagini_match/$matchId")
        immagini.getBytes(MAX_SIZE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            img_match_view.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("MatchView", "Impossibile recuperare l'immagine")
        }

        btn_match_view_partecipate_leave.setOnClickListener(this)
        btn_match_view_delete.setOnClickListener(this)
        sw_match_notifications.setOnClickListener(this)
    }

    private fun getPartecipants(db:FirebaseFirestore){
        matchReference = db.collection("partite").document(matchId)
        matchReference.get().addOnSuccessListener { document ->
            if (document != null) {
                //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                //println("dati: ${document.data}")
                partita = document.toObject(Match::class.java)!!


                if (document.get("partecipants") != null) {
                    partita.partecipants = document.get("partecipants") as ArrayList<String>


                    val participantsNames = ArrayList<String>()
                    for (utenteInPartita in partita.partecipants) {
                        val userReference = db.collection("utenti").document(utenteInPartita)
                        userReference.get().addOnSuccessListener { doc ->
                            val userInPartita = doc.toObject(User::class.java)!!
                            participantsNames.add(userInPartita.name.toString())
                            /*val participantsListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, participantsNames)*/


                            val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, participantsNames) {
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    val view = super.getView(position, convertView, parent)
                                    val textView = view.findViewById<View>(android.R.id.text1) as TextView
                                    textView.setTextColor((0xffffffff).toInt())
                                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(200, 10)
                                    params.setMargins(0, 0, 0, 0)
                                    textView.setPadding(0, 0, 0, 0)
                                    textView.setTextSize(1, 22F)
                                    return view
                                }
                            }
                            list_match_view_participants.adapter = adapter
                        }
                    }
                } else {
                    partita.partecipants = ArrayList()
                    TextPartecipanti = "ancora nessun partecipante"
                }
                tv_match_view_partecipants.text = "${partita.partecipants.size.toString()}/${partita.available.toString()}"
                partecipa = partita.partecipants.contains(firebaseUser.uid)
                btn_match_view_partecipate_leave.text = if (partecipa) "Lascia partita" else "Partecipa"
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_match_view_partecipate_leave -> {
                val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                val db = FirebaseFirestore.getInstance()
                db.firestoreSettings = settings
                if (partecipa) {
                    partita.partecipants.remove(firebaseUser.uid)
                    matchReference.update("partecipants", partita.partecipants)
                    getPartecipants(db)
                } else {
                    partita.partecipants.add(firebaseUser.uid)
                    matchReference.update("partecipants", partita.partecipants)
                    getPartecipants(db)
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
            R.id.sw_match_notifications -> {
                scheduleNotification(getNotification(partita), partita.matchDate)
            }
        }
    }

    private fun scheduleNotification(notification: Notification, matchTime: Long) {
        val notificationIntent = Intent(this, MatchNotificationManager::class.java)
        notificationIntent.putExtra("NOTIFICATION_ID", partita.creationDate.toInt())
        notificationIntent.putExtra("NOTIFICATION", notification)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val delay = matchTime - Calendar.getInstance().timeInMillis
        val futureInMillis = SystemClock.elapsedRealtime() + delay - 7200000 // due ore di anticipo
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(match: Match): Notification {
        val intent = Intent(this, MatchViewActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("MATCH_ID", matchId)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, MatchNotificationManager.MATCH_CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Manca poco alla partita!")
                .setContentText(match.matchName + " - " + dateTimeFormat.format(dateTime.time))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
        return builder.build()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val MAX_SIZE = 8 * 1024 * 1024.toLong()
    }




}