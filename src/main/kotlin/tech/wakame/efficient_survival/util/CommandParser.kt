package tech.wakame.efficient_survival.util

import java.util.*

/**
 * string list classify params and options (starts with "-" or "--")
 *
 * @return params array and options map
 */
fun Array<out String>.toParamsAndOptions(): Pair<Array<String>, Map<String, String?>> {
    fun isOption(arg: String?) = arg != null && (arg.startsWith("-") || arg.startsWith("--"))

    val argsList = LinkedList<String>().also { it.addAll(this) }
    val params = mutableListOf<String>()
    val options = mutableMapOf<String, String?>()

    // regards args until starts options
    while (argsList.isNotEmpty() && !isOption(argsList.peekFirst())) {
        params.add(argsList.pollFirst())
    }

    // rest of string list are options
    while (argsList.isNotEmpty()) {
        val opt = argsList.pollFirst()
        if (argsList.isNotEmpty()) {
            if (!isOption(argsList.peekFirst())) {
                // --opt val ... -> ["opt"] = "val"
                options[opt] = argsList.pollFirst()
            } else {
                // --opt --opt2 ... -> ["opt"] = null, ["opt2"] = ...
                options[opt] = null
            }
        } else {
            options[opt] = null
        }
    }
    return params.toTypedArray() to options.toMap()
}