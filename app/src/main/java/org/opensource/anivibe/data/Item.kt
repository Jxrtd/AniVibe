package org.opensource.anivibe.data

data class Item(
    val profileImagePath: String?,   // full‐path to the image in internal storage
    var username: String,            // Change from val to var
    val description: String          // the post text
)