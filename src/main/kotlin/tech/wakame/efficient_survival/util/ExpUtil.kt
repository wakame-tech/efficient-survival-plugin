package tech.wakame.efficient_survival.util

import org.bukkit.entity.Player

/**
 * A utility for managing player experience.
 */
object ExperienceUtil {
    /**
     * Calculate a player's total experience based on level and progress to next.
     *
     * @param player the Player
     * @return the amount of experience the Player has
     *
     * @see [ExperienceUtil.Leveling_up](http://minecraft.gamepedia.com/Experience.Leveling_up)
     */
    fun getExp(player: Player): Int {
        return (getExpFromLevel(player.level)
                + Math.round(getExpToNext(player.level) * player.exp))
    }

    /**
     * Calculate total experience based on level.
     *
     * @param level the level
     * @return the total experience calculated
     *
     * @see [ExperienceUtil.Leveling_up](http://minecraft.gamepedia.com/Experience.Leveling_up)
     */
    fun getExpFromLevel(level: Int): Int {
        if (level > 30) {
            return (4.5 * level * level - 162.5 * level + 2220).toInt()
        }
        return if (level > 15) {
            (2.5 * level * level - 40.5 * level + 360).toInt()
        } else level * level + 6 * level
    }

    /**
     * Calculate level (including progress to next level) based on total experience.
     *
     * @param exp the total experience
     * @return the level calculated
     */
    fun getLevelFromExp(exp: Long): Double {
        val level = getIntLevelFromExp(exp)

        // Get remaining exp progressing towards next level. Cast to float for next bit of math.
        val remainder = exp - getExpFromLevel(level).toFloat()

        // Get level progress with float precision.
        val progress = remainder / getExpToNext(level)

        // Slap both numbers together and call it a day. While it shouldn't be possible for progress
        // to be an invalid value (value < 0 || 1 <= value)
        return level.toDouble() + progress
    }

    /**
     * Calculate level based on total experience.
     *
     * @param exp the total experience
     * @return the level calculated
     */
    fun getIntLevelFromExp(exp: Long): Int {
        if (exp > 1395) {
            return ((Math.sqrt(72 * exp - 54215.0) + 325) / 18).toInt()
        }
        if (exp > 315) {
            return (Math.sqrt(40 * exp - 7839.0) / 10 + 8.1).toInt()
        }
        return if (exp > 0) {
            (Math.sqrt(exp + 9.0) - 3).toInt()
        } else 0
    }

    /**
     * Get the total amount of experience required to progress to the next level.
     *
     * @param level the current level
     *
     * @see [ExperienceUtil.Leveling_up](http://minecraft.gamepedia.com/Experience.Leveling_up)
     */
    private fun getExpToNext(level: Int): Int {
        if (level >= 30) {
            // Simplified formula. Internal: 112 + (level - 30) * 9
            return level * 9 + 158
        }
        return if (level >= 15) {
            // Simplified formula. Internal: 37 + (level - 15) * 5
            level * 5 - 38
        } else level * 2 + 7
        // Internal: 7 + level * 2
    }

    /**
     * Change a Player's experience.
     *
     *
     * This method is preferred over [Player.giveExp].
     * <br></br>In older versions the method does not take differences in exp per level into account.
     * This leads to overlevelling when granting players large amounts of experience.
     * <br></br>In modern versions, while differing amounts of experience per level are accounted for, the
     * approach used is loop-heavy and requires an excessive number of calculations, which makes it
     * quite slow.
     *
     * @param player the Player affected
     * @param exp the amount of experience to add or remove
     */
    fun changeExp(player: Player, exp: Int) {
        var exp = exp
        exp += getExp(player)
        if (exp < 0) {
            exp = 0
        }
        val levelAndExp = getLevelFromExp(exp.toLong())
        val level = levelAndExp.toInt()
        player.level = level
        player.exp = (levelAndExp - level).toFloat()
    }
}