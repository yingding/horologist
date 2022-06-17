package com.google.android.horologist.nav

sealed class NavScreen (val route: String) {
    object Menu: NavScreen("menu")
    object Activity: NavScreen("activity")
    object Graph: NavScreen("graph")
    object Setting: NavScreen("Setting")
}