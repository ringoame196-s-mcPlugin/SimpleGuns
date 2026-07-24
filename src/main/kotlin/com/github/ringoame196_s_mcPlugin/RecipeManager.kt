package com.github.ringoame196_s_mcPlugin

import org.bukkit.Bukkit

object RecipeManager {
    fun registerRecipes(gunList: List<Gun>) {
        for (gun in gunList) {
            val recipe = gun.recipe

            if (Bukkit.getRecipe(recipe.key) != null) {
                Bukkit.removeRecipe(recipe.key)
            }

            Bukkit.addRecipe(recipe)
        }
    }

    fun unregisterGunRecipes(gunList: List<Gun>) {
        for (gun in gunList) {
            val key = gun.recipe.key
            Bukkit.removeRecipe(key)
        }
    }
}
