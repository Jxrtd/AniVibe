package org.opensource.anivibe.data

import org.opensource.anivibe.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ListOfPost(
    var username: String = "",
    var post: String = "",
    var photo: Int = R.drawable.boy,
    var timestamp: Long = System.currentTimeMillis()  // Added timestamp field
)
