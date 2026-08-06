package com.github.ringoame196_s_mcPlugin.models

import org.bukkit.Material
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack

interface GunItem {
    val id: String
    val item: ItemStack
    val displayName: String
    val material: Material

    val recipe: CraftingRecipe? // 定型レシピ、無定形レシピ どちらも使えるようにレシピを持たせておく
}
