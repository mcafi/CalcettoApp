package com.mcafi.calcetto.model

import android.icu.text.SimpleDateFormat
import java.util.*

class DateTime(val year: Int = 0, val month: Int = 0, val day: Int = 0, val hour: Int = 0, val minute: Int = 0){
    private val dateTimeFormat = java.text.SimpleDateFormat("dd/MMM/yyyy - HH:mm", Locale("it"))

    constructor(c: Calendar) : this(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))

    override fun toString(): String {
        val datetime: Calendar = Calendar.getInstance()
        datetime.set(this.year, this.month, this.day, this.hour, this.minute)
        return dateTimeFormat.format(datetime.time)
    }

    public fun isBefore(): Boolean {
        val datetime: Calendar = Calendar.getInstance()
        datetime.set(this.year, this.month, this.day, this.hour, this.minute)
        return Calendar.getInstance().before(datetime)
    }

    public fun isAfter(): Boolean {
        val datetime: Calendar = Calendar.getInstance()
        datetime.set(this.year, this.month, this.day, this.hour, this.minute)
        return Calendar.getInstance().after(datetime)
    }
}