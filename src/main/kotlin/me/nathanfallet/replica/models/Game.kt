/*
 *  Copyright (C) 2023 FALLET Nathan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */

package me.nathanfallet.replica.models

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.UUID

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import me.nathanfallet.replica.Replica

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction
import com.comphenix.protocol.wrappers.WrappedChatComponent

data class Game(
    val id: Int,
    var state: GameState,
    var currentCountValue: Int,
    val signs: MutableList<Location>
) {

    constructor(id: Int) : this(id, GameState.waiting, 0, mutableListOf())

    val players: List<UUID>
		get() {
			return Bukkit.getOnlinePlayers().map { it.uniqueId }.filter {
				val zp = Replica.instance?.getPlayer(it)
				zp?.currentGame == id && (state != GameState.inGame && state != GameState.finished || zp.playing) && !zp.buildmode
			}
		}

    val allPlayers: List<UUID>
		get() {
			return Bukkit.getOnlinePlayers().map { it.uniqueId }.filter {
				val zp = Replica.instance?.getPlayer(it)
				zp?.currentGame == id && !zp.buildmode
			}
		}

    fun updateSigns() {
		val players = players
        val i = signs.iterator()
        while (i.hasNext()) {
            val b = i.next().block
            if (b.type == Material.OAK_SIGN) {
                val s = b.state as Sign
                s.setLine(0, "§4[Replica]")
                s.setLine(1, Replica.instance?.messages?.get("sign-line-game")?.replace("%d", id.toString() + "") ?: id.toString())
                s.setLine(2, players.size.toString() + "/10")
                s.setLine(3, state.text?.replace("%d", currentCountValue.toString() + "") ?: currentCountValue.toString())
                s.update()
            } else {
                i.remove()
            }
        }
    }

    fun makeClay(color: Int): Material {
        return when (color) {
            0 -> Material.WHITE_TERRACOTTA
            1 -> Material.ORANGE_TERRACOTTA
            2 -> Material.MAGENTA_TERRACOTTA
            3 -> Material.LIGHT_BLUE_TERRACOTTA
            4 -> Material.YELLOW_TERRACOTTA
            5 -> Material.LIME_TERRACOTTA
            6 -> Material.PINK_TERRACOTTA
            7 -> Material.GRAY_TERRACOTTA
            8 -> Material.LIGHT_GRAY_TERRACOTTA
            9 -> Material.CYAN_TERRACOTTA
            10 -> Material.PURPLE_TERRACOTTA
            11 -> Material.BLUE_TERRACOTTA
            12 -> Material.BROWN_TERRACOTTA
            13 -> Material.GREEN_TERRACOTTA
            14 -> Material.RED_TERRACOTTA
            else -> Material.BLACK_TERRACOTTA
        }
    }

    fun loadPlots() {
        for (i in 0..19) {
            for (x in 5..12) {
                for (z in 5..12) {
                    Location(
						Bukkit.getWorld("Replica"),
						(x + Replica.distance * 16 * (id - 1)).toDouble(),
						64.0,
						(z + i * 32).toDouble()
					).block.type = Material.AIR
                }
            }
            for (y in 0..7) {
                for (z in 5..12) {
                    Location(
						Bukkit.getWorld("Replica"),
						(14 + Replica.distance * 16 * (id - 1)).toDouble(),
						(66 + y).toDouble(),
						(z + i * 32).toDouble()
					).block.type = Material.AIR
                }
            }
        }
    }

	fun breakPlot(col: Int) {
		val column = col - 1
		for (x in 5..12) {
			for (z in 5..12) {
				Location(
					Bukkit.getWorld("Replica"),
					(x + Replica.distance * 16 * (id - 1)).toDouble(),
					64.0,
					(z + column * 32).toDouble()
				).block.type = Material.AIR
			}
		}
	}

	fun drawPlot(col: Int, p: Picture) {
		val column = col - 1
		for (y in 0..7) {
			for (z in 5..12) {
				val b = Location(
					Bukkit.getWorld("Replica"),
					(14 + Replica.distance * 16 * (id - 1)).toDouble(),
					(66 + (7 - y)).toDouble(),
					(z + column * 32).toDouble()
				).block
				b.type = makeClay(p.blocks[Pair(z - 5, y)] ?: 0)
			}
		}
	}

	fun isCompletingPlot(col: Int): Boolean {
		val column = col - 1
		for (x in 0..7) {
			for (y in 0..7) {
				val b = Location(
					Bukkit.getWorld("Replica"),
					(14 + Replica.distance * 16 * (id - 1)).toDouble(),
					(66 + (7 - y)).toDouble(),
					((7 - x) + column * 32 + 5).toDouble()
				).block
				val b2 = Location(
					Bukkit.getWorld("Replica"),
					(5 + (7 - y) + Replica.distance * 16 * (id - 1)).toDouble(),
					64.0,
					((7 - x) + column * 32 + 5).toDouble()
				).block
				if (b.type != b2.type) {
					return false
				}
			}
		}
		return true
	}

	fun containsColor(col: Int, color: Int): Boolean {
		val column = col - 1
		for (x in 0..7) {
			for (y in 0..7) {
				val b = Location(
					Bukkit.getWorld("Replica"),
					(14 + Replica.distance * 16 * (id - 1)).toDouble(),
					(66 + (7 - y)).toDouble(),
					((7 - x) + column * 32 + 5).toDouble()
				)
				if (b.block.type == makeClay(color)) {
					return true
				}
			}
		}
		return false
	}

	fun start() {
		allPlayers.forEach { uuid ->
			val zp = Replica.instance?.getPlayer(uuid)
			zp?.playing = true
		}
		state = GameState.inGame
		loadDraw()
	}

	fun stop() {
		if (state == GameState.inGame) {
			state = GameState.finished
			Bukkit.getPlayer(players[0])?.let { player ->
				Bukkit.broadcastMessage("§7${Replica.instance?.messages?.get("chat-win-public")?.replace("%s", player.name)}")
				player.inventory.clear()
				player.updateInventory()
				player.gameMode = GameMode.SPECTATOR
				val pc = PacketContainer(PacketType.Play.Server.TITLE)
				pc.titleActions.write(0, TitleAction.TITLE)
				pc.chatComponents.write(0, WrappedChatComponent.fromText("§a${Replica.instance?.messages?.get("chat-win-private")}"))
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc)
				} catch (e: InvocationTargetException) {
					e.printStackTrace()
				}
			}
			currentCountValue = 0
			loadPlots()
			Bukkit.getScheduler().scheduleSyncDelayedTask(Replica.instance!!, Runnable {
				allPlayers.forEach { uuid ->
					Bukkit.getPlayer(uuid)?.let { player ->
						Replica.instance?.getPlayer(uuid)?.let { zp ->
							zp.playing = false
							zp.finish = false
							zp.plot = 0
							zp.currentGame = 0
							Replica.instance?.getConfig()?.getString("spawn-command")?.let { command ->
								Bukkit.dispatchCommand(player, command)
							}
							player.gameMode = GameMode.SURVIVAL
							player.inventory.clear()
							player.updateInventory()
						}
					}
				}
				state = GameState.waiting
			}, 100)
		}
	}

	fun loadDraw() {
		val players = players
		val p = Replica.instance?.pictures?.random()
		loadPlots()
		draw(p, players.size)
		var plot = 1
		val pc = PacketContainer(PacketType.Play.Server.TITLE)
		pc.titleActions.write(0, TitleAction.TITLE)
		pc.chatComponents.write(0, WrappedChatComponent.fromText("§6${p?.name}"))
		players.forEach { uuid ->
			val player = Bukkit.getPlayer(uuid)
			val zp = Replica.instance?.getPlayer(uuid)
			val l = Location(
				Bukkit.getWorld("Replica"),
				(4 + Replica.distance * 16 * (id - 1)).toDouble(),
				65.toDouble(),
				((plot - 1) * 32 + 9).toDouble()
			)
			l.yaw = -90f
			player?.teleport(l)
			player?.gameMode = GameMode.SURVIVAL
			zp?.plot = plot
			zp?.finish = false
			zp?.playing = true
			player?.inventory?.clear()
			player?.inventory?.addItem(ItemStack(Material.IRON_PICKAXE))
			for (i in 0..15) {
				player?.inventory?.addItem(ItemStack(makeClay(i), 64))
			}
			player?.updateInventory()
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc)
			} catch (e: InvocationTargetException) {
				e.printStackTrace()
			}
			plot++
		}
	}

	fun verifNext() {
		val players = players
		var number = players.size
		var current = 0
		var no: UUID? = null
		players.forEach { uuid ->
			val zp = Replica.instance?.getPlayer(uuid)
			if (zp?.finish == true) {
				current++
			} else {
				no = uuid
			}
		}
		if (number == 0 || number == 1) {
			stop()
		} else if (current >= number - 1) {
			no?.let {
				val nop = Bukkit.getPlayer(no!!)
				val zp = Replica.instance?.getPlayer(no!!)
				zp?.playing = false
				zp?.finish = false
				zp?.plot = 0
				allPlayers.forEach { uuid ->
					val p = Bukkit.getPlayer(uuid)
					p?.sendMessage("§7${Replica.instance?.messages?.get("chat-lose-public")?.replace("%s", nop?.name ?: "")}")
				}
				nop?.inventory?.clear()
				nop?.updateInventory()
				nop?.gameMode = GameMode.SPECTATOR
				val pc = PacketContainer(PacketType.Play.Server.TITLE)
				pc.titleActions.write(0, TitleAction.TITLE)
				pc.chatComponents.write(0, WrappedChatComponent.fromText("§a${Replica.instance?.messages?.get("chat-lose-private")}"))
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(nop, pc)
				} catch (e: InvocationTargetException) {
					e.printStackTrace()
				}
				nop?.sendMessage("§7${Replica.instance?.messages?.get("chat-lose-private")}")
			}
			if (number == 2) {
				stop()
			} else {
				loadDraw()
			}
		}
	}

	fun draw(picture: Picture?, limit: Int) {
		picture?.let {
			for (i in 1..limit) {
				drawPlot(i, picture)
			}
		}
	}

}
