package com.example.findmydost.mvvm.model

import android.net.Uri
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date
import java.io.Serializable


data class fireBaseUser constructor(
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: Uri? = null,
    val authType: String? = null,
    val phoneNumber: String? = null,
    val uid: String? = null,
    @ServerTimestamp
    val serverTimeStamo: Date? = null
) : Serializable {


}