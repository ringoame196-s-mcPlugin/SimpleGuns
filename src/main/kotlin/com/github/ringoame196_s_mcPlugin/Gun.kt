package com.github.ringoame196_s_mcPlugin

import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.ItemStack

interface Gun {
    val id: String
    val gun: ItemStack
    val recipe: CraftingRecipe // 定型レシピ、無定形レシピ どちらも使えるように銃にレシピを持たせておく
}
