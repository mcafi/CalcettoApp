package com.mcafi.calcetto





import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mcafi.calcetto.model.Match
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class MatchAdapterProfile(context: Context, private val dataSource: ArrayList<Match>): BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val dateTimeFormat = SimpleDateFormat("dd/MMM/yyyy - HH:mm", Locale("it"))
    private val c = Calendar.getInstance()
    private val mAuthReg = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.match_item_profile_xml, parent, false)
        val match = getItem(position) as Match
        firebaseUser = mAuthReg.currentUser!!
        val matchDate = rowView.findViewById(R.id.match_date) as TextView
        val matchAvailable = rowView.findViewById(R.id.match_available) as TextView
        val matchCreator = rowView.findViewById(R.id.match_creator) as TextView

        c.timeInMillis = match.matchDate

        matchDate.text = dateTimeFormat.format(c.time)
        matchAvailable.text = (match.available - match.partecipants.size).toString()




        if(match.partecipants.contains(firebaseUser.uid) ) {
            matchDate.setTextColor((0xff5EBCC9).toInt())
            matchCreator.text = "partecipante"
        }
        
        if(match.creator == firebaseUser.uid ) {
            matchDate.setTextColor((0xff00ff00).toInt())
            matchCreator.text = "creatore    "


        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}