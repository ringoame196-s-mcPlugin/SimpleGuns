package com.github.ringoame196_s_mcPlugin

import org.bukkit.Material

class SimpleGun(name: String) : Gun {
    override val id = "simple_gun"
    override val gun = GunManager.makeGun(Material.IRON_HOE, id, name)
}
