package com.mcafi.calcetto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mcafi.calcetto.model.Match
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MatchAdapter(context: Context, private val dataSource: ArrayList<Match>): BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val datetime = Calendar.getInstance()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.match_item, parent, false)
        val match = getItem(position) as Match

        val nameMatch = rowView.findViewById(R.id.tv_match_name_match) as TextView
        val matchAvailable = rowView.findViewById(R.id.tv_match_available) as TextView
        val matchNotes = rowView.findViewById(R.id.tv_match_notes) as TextView
        val matchDay = rowView.findViewById(R.id.Day) as TextView
        val matchDate = rowView.findViewById(R.id.Date) as TextView
        val matchTime = rowView.findViewById(R.id.Time) as TextView

        datetime.timeInMillis = match.matchDate


        matchDay.text=SimpleDateFormat("dd",Locale("it")).format(datetime.time)
        matchDate.text=SimpleDateFormat("MMMyy",Locale("it")).format(datetime.time)
        matchTime.text=SimpleDateFormat("HH:mm",Locale("it")).format(datetime.time)
        nameMatch.text=match.matchName.capitalize()
        matchAvailable.text = "partecipanti: "+"${match.partecipants.size.toString()}/${match.available.toString()}"
        matchNotes.text = match.notes



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