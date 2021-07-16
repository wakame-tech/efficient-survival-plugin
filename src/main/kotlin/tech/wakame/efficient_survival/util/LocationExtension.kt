package tech.wakame.efficient_survival.util

import org.bukkit.Location

/**
 *  [Location] to easy string, such as "(X, Y, Z)"
 */
fun Location.inspect() = "(${x.toInt()}, ${y.toInt()}, ${z.toInt()})"