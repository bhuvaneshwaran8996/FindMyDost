package com.example.findmydost.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

public fun Fragment.showToast(context:Context,msg:String){

    Toast.makeText(context,msg,Toast.LENGTH_LONG).show()

}