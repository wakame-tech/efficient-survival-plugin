package tech.wakame.efficient_survival.caveanalyzer

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import tech.wakame.efficient_survival.namedlocation.INamedLocationUseCase
import tech.wakame.efficient_survival.util.toParamsAndOptions

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.wakame.efficient_survival.EfficientSurvival
import tech.wakame.efficient_survival.util.CommandHandler
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis


class CaveAnalyzerCommandHandler: CommandHandler() {
    init {
        handlers["caveanalysis"] = ::caveAnalysis
    }

    private fun _caveAnalysisImpl(sender: Player, start: Location, options: Map<String, String?>) {
        fun locationDecorator(l: Location) = Triple(l.blockX, l.blockY, l.blockZ)

        var count = 0
        val searchLimit = options["--limit"]?.toIntOrNull() ?: 10_0000
        val logInterval  = options["--interval"]?.toIntOrNull() ?: 1_0000
        val radius  = options["--radius"]?.toIntOrNull() ?: 200
        val setTorch = "--set-torch" in options.keys
        val directions = arrayOf(
                BlockFace.DOWN,
                BlockFace.UP,
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST
        )

        val history = mutableSetOf<Triple<Int, Int, Int>>()

        fun bfs(pos: Location) {
            val queue = LinkedList<Location>()
            queue.push(pos)
            history.add(locationDecorator(pos))

            while (queue.isNotEmpty()) {
                if (count % logInterval == 0) {
                    sender.sendMessage("searching $count blocks ...")
                }

                val loc = queue.pop()
                if (searchLimit < count++) {
                    sender.sendMessage("search block limit ($searchLimit blocks) exceeded")
                    break
                }

                if (radius < pos.distance(loc)) {
                    continue
                }

                // set torch
                if (setTorch && !loc.block.getRelative(BlockFace.DOWN).isEmpty && loc.block.lightLevel <= 7) {
                    loc.block.type = Material.TORCH
                    Thread.sleep(100)
                }

                directions.forEach {
                    val next = loc.block.getRelative(it, 1)
                    if (next.isEmpty && next.location.blockY <= pos.blockY && locationDecorator(next.location) !in history) {
                        history.add(locationDecorator(next.location))
                        queue.push(next.location)
                    }
                }
            }
        }

        val time = measureTimeMillis {
            bfs(start)
        }

        // filter only floor
        val floors = history.filter { it.copy(second = it.second - 1) !in history }

        if ("--dump" in options.keys) {
            val fileName = options["--dump"] ?: "cave"
            // write plots
            val src = File("$fileName.xyz")
            sender.sendMessage("history saved at ${src.absolutePath}")
            src.absoluteFile.writeText(floors.joinToString("\n") { (x, y, z) -> "$x $y $z" })
        }

        if (history.isEmpty()) {
            return
        }

        val minY = history.minBy { it.second }!!.second
        val maxY = history.maxBy { it.second }!!.second
        val minX = history.minBy { it.first }!!.first
        val maxX = history.maxBy { it.first }!!.first
        val minZ = history.minBy { it.third }!!.third
        val maxZ = history.maxBy { it.third }!!.third

        sender.sendMessage("[caveAnalysis] finished in $time ms")
        sender.sendMessage("[caveAnalysis] cave size: ${history.size} blocks, floor size: ${floors.size}")
        sender.sendMessage("[caveAnalysis] cave region: ${abs(maxX - minX)} x ${abs(maxY - minY)} x ${abs(maxZ - minZ)}")
    }

    /**
     * analysis cave
     * @return result of command
     */
    private fun caveAnalysis(sender: CommandSender, params: Array<String>, options: Map<String, String?>): Boolean {
        if (sender !is Player) return false

        Bukkit.getScheduler().runTask(EfficientSurvival.plugin, Runnable {
            _caveAnalysisImpl(sender, sender.location, options)
        })

        return true
    }
}