package com.example.findmydost.util




data class LoginStatus(public val msg:String, public val  loginStatus: LoginState)


public enum class LoginState{

 LOGGED_IN_FB,
 LOGGED_IN_GOOGLE,
 LOGGED_OUT,
 LOADING,
 LOGIN_FAILED


}