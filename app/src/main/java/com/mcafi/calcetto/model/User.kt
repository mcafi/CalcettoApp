package com.mcafi.calcetto.model

import java.util.*

class User(var email: String? = "", var name: String? = "", var username: String? = "") {

    fun toMap(): Map<String, Any?> {
        val map: MutableMap<String, Any?> = HashMap()
        map["email"] = email
        map["name"] = name
        map["username"] = username
        return map
    }
}