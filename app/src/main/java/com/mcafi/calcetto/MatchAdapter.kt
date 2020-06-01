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
    private val dateTimeFormat = SimpleDateFormat("dd/MMM/yyyy - HH:mm", Locale("it"))
    private val c = Calendar.getInstance()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.match_item, parent, false)
        val match = getItem(position) as Match

        val matchDate = rowView.findViewById(R.id.match_date) as TextView
        val matchAvailable = rowView.findViewById(R.id.match_available) as TextView
        val matchNotes = rowView.findViewById(R.id.match_notes) as TextView

        c.set(match.date.year, match.date.month, match.date.day, match.date.hour, match.date.minute)

        matchDate.text = dateTimeFormat.format(c.time)
        matchAvailable.text = (match.available - match.participants.size).toString()
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