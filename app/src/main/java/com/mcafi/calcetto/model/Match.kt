package com.mcafi.calcetto.model

import kotlin.collections.ArrayList

class Match(var user: String = "", var date: MatchDateTime? = null, var notes: String = "", var participants: List<String> = ArrayList<String>(), var available: Int = 0) {
}