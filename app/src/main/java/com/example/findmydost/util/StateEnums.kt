package com.example.findmydost.util


enum class Status{
 ERROR,
 LOADING,
 SUCCESS
}
enum class LoggedInMode(val type: Int) {
 LOGGED_IN_MODE_LOGGED_OUT(0), LOGGED_IN_MODE_GOOGLE(1), LOGGED_IN_MODE_FB(2), LOGGED_IN_MODE_SERVER(
  3
 )

}