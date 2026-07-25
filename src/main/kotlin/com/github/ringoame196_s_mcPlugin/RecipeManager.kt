package com.github.ringoame196_s_mcPlugin

import org.bukkit.Bukkit

object RecipeManager {
    fun registerRecipes(gunItemList: List<GunItem>) {
        for (gunItem in gunItemList) {
            val recipe = gunItem.recipe

            if (Bukkit.getRecipe(recipe.key) != null) {
                Bukkit.removeRecipe(recipe.key)
            }

            Bukkit.addRecipe(recipe)
        }
    }
}
