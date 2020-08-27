package com.mcafi.calcetto.model

import kotlin.collections.ArrayList

class Match(
        var creator: String = "",
        var creationDate: Long = 0,
        var matchDate: Long = 0,
        var notes: String = "",
        var participants: MutableList<String> = ArrayList(),
        var available: Int = 0,
        var place: MatchPlace = MatchPlace(),
        var matchName: String = "",
        var id: String = ""
) {
}