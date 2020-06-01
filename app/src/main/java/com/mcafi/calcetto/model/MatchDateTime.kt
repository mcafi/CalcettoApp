package com.mcafi.calcetto.model

import java.util.*

class MatchDateTime (val year: Int = 0, val month: Int = 0, val day: Int = 0, val hour: Int = 0, val minute: Int = 0){
    constructor(c: Calendar) : this(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
}