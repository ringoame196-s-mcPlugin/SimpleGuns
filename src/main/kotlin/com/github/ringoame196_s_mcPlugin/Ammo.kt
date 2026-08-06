package com.github.ringoame196_s_mcPlugin

interface Ammo : GunItem {
    val ammoCost: Int get() = 1
    val cooldownSeconds: Double
}
