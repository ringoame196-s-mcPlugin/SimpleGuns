package com.github.ringoame196_s_mcPlugin

import org.bukkit.NamespacedKey

// キーの定義（ファイル内または object 内で閉じる）
enum class GunKey(val keyName: String) {
    GUN_ID("gun_item_id"),
    GUN_AMMO("gun_ammo"),
    SELECT_SLOT("select_gun_slot"),
    GUN_SLOTS("gun_slots");

    val key: NamespacedKey get() = NamespacedKey(GunManager.plugin, keyName)
}
