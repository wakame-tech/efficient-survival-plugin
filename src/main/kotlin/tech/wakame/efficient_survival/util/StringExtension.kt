package tech.wakame.efficient_survival.util

import org.bukkit.ChatColor

/*
    replace to color code in a string
*/
fun String.colored(): String {
    fun String.replace(map: Map<String, Any>) = map.toList().fold(this) { text, (key, value) ->
        text.replace(key.toRegex(), value.toString())
    }

    return this.replace(mapOf(
            "black\\{" to ChatColor.WHITE,
            "red\\{" to ChatColor.RED,
            "aqua\\{" to ChatColor.AQUA,
            "green\\{" to ChatColor.GREEN,
            "yellow\\{" to ChatColor.YELLOW,
            "blue\\{" to ChatColor.BLUE,
            "white\\{" to ChatColor.WHITE,
            "bold\\{" to ChatColor.BOLD,
            "italic\\{" to ChatColor.ITALIC,
            "gray\\{" to ChatColor.GRAY,
            "-\\{" to ChatColor.STRIKETHROUGH,
            "\\}" to ChatColor.RESET
    ))
}