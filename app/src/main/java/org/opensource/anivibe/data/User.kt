package org.opensource.anivibe.data

data class User(
    var username: String,
    var email: String,
    var education: String,
    var hometown: String,
    var location: String,
    var birthdate: String,
    var profileImagePath: String? = null
)