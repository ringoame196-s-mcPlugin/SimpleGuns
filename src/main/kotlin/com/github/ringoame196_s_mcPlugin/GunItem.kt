package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack

interface GunItem {
    val id: String
    val item: ItemStack
    val recipe: CraftingRecipe // 定型レシピ、無定形レシピ どちらも使えるようにレシピを持たせておく
}
