package tech.wakame.efficient_survival.portableexp

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tech.wakame.efficient_survival.util.CommandHandler
import tech.wakame.efficient_survival.util.ExperienceUtil

class PortableExpCommandHandler : CommandHandler() {
    init {
        handlers["expconvert"] = ::convertPlayerExpToExpBottle
    }

    private fun convertPlayerExpToExpBottle(
        sender: CommandSender,
        params: Array<String>,
        options: Map<String, String?>
    ): Boolean {
        if (sender !is Player)
            return false

        fun partitionByK(n: Int, k: Int): Array<Int> {
            require(k > 0)
            val (d, m) = n.div(k) to n % k
            val res = Array(d) { k }
            if (m == 0) {
                return res
            }
            return res + arrayOf(m)
        }

        val totalExp = ExperienceUtil.getExp(sender)
        val bottleSize = totalExp.div(7)
        val bottles = partitionByK(bottleSize, 64)
            .map { ItemStack(Material.EXPERIENCE_BOTTLE, it) }
            .toTypedArray()

        sender.sendMessage("totalExp: $totalExp, bottles: $bottleSize")
        sender.inventory.addItem(*bottles)
            .values
            .forEach {
                sender.world.dropItemNaturally(sender.location, it)
            }

        // reset player exp
        sender.exp = 0f
        sender.level = 0
        return true
    }
}