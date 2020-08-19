package com.mcafi.calcetto.model

import kotlin.collections.ArrayList

class Match(
        var creator: String = "",
        var creationDate: DateTime = DateTime(),
        var matchDate: DateTime = DateTime(),
        var notes: String = "",
        var participants: List<String> = ArrayList<String>(),
        var available: Int = 0,
        var place: MatchPlace = MatchPlace(),
        var id: String = ""
) {
}