package com.mcafi.calcetto.model

import com.google.android.libraries.places.api.model.Place
import kotlin.collections.ArrayList

class Match(
        var creator: String = "",
        var creationDate: DateTime = DateTime(),
        var matchDate: DateTime = DateTime(),
        var notes: String = "",
        var participants: List<String> = ArrayList<String>(),
        var available: Int = 0,
        var id: String = "",
        var place: Place) {
}