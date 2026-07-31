package com.github.ringoame196_s_mcPlugin

import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

/**
 * ItemMeta を包んで銃の操作をカプセル化するラッパークラス
 */

class GunMeta(val rawMeta: ItemMeta) {
    private enum class GunKey(val keyName: String) {
        GUN_ID("gun_item_id"),
        GUN_AMMO("gun_ammo"),
        SELECT_SLOT("select_gun_slot"),
        GUN_SLOTS("gun_slots");

        val key: NamespacedKey get() = NamespacedKey(GunManager.plugin, keyName)
    }

    // 銃のID
    var id: String?
        get() = rawMeta.persistentDataContainer.get(GunKey.GUN_ID.key, PersistentDataType.STRING)
        set(value) {
            if (value == null) {
                rawMeta.persistentDataContainer.remove(GunKey.GUN_ID.key)
            } else {
                rawMeta.persistentDataContainer.set(GunKey.GUN_ID.key, PersistentDataType.STRING, value)
            }
        }

    // 残弾数
    var ammo: Int
        get() = rawMeta.persistentDataContainer.get(GunKey.GUN_AMMO.key, PersistentDataType.INTEGER) ?: 0
        set(value) {
            val safeValue = value.coerceAtLeast(0)
            rawMeta.persistentDataContainer.set(GunKey.GUN_AMMO.key, PersistentDataType.INTEGER, safeValue)
        }

    var selectSlot: Int
        get() = rawMeta.persistentDataContainer.get(GunKey.SELECT_SLOT.key, PersistentDataType.INTEGER) ?: 1
        set(value) {
            val safeValue = value.coerceAtLeast(1)
            rawMeta.persistentDataContainer.set(GunKey.SELECT_SLOT.key, PersistentDataType.INTEGER, safeValue)
        }

    // Loreの弾数表示を更新する便利メソッド
    fun updateAmmoLore(maxAmmo: Int) {
        rawMeta.lore = listOf("$ammo/$maxAmmo")
    }

    // 弾薬を減らす処理
    fun reduceAmmo(amount: Int) {
        ammo -= amount
    }
}

// ItemMeta から GunMeta を簡単に呼び出せるようにする拡張プロパティ
val ItemMeta.gun: GunMeta
    get() = GunMeta(this)
