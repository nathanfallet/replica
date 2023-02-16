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

package me.nathanfallet.replica

import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.Random
import java.util.UUID
import java.util.concurrent.Callable

import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bukkit.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

import me.nathanfallet.replica.commands.Cmd
import me.nathanfallet.replica.events.*
import me.nathanfallet.replica.models.*

class Replica: JavaPlugin() {

	companion object {
		val distance = 5
		var instance: Replica? = null
	}

	private val players = mutableListOf<ZabriPlayer>()

	val pictures = mutableListOf<Picture>()
	val games = mutableListOf<Game>()
	val messages = Messages()

	val countdown: Int
		get() = getConfig().getInt("countdown").takeIf { it >= 5 } ?: 30

	fun getPlayer(uuid: UUID): ZabriPlayer? {
		return players.firstOrNull { it.uuid == uuid }
	}
	
	fun initPlayer(player: Player) {
		players.add(ZabriPlayer(player))
	}
	
	fun uninitPlayer(player: ZabriPlayer) {
		players.removeIf { it.uuid == player.uuid }
	}

	fun joinPlayer(player: Player, game: Game) {
		if (game.state != GameState.waiting && game.state != GameState.startCount) {
			player.sendMessage("§c" + messages.get("chat-game-full"))
			return
		}
		if (game.players.size >= 10) {
			player.sendMessage("§c" + messages.get("chat-game-full"))
			return
		}
		getPlayer(player.uniqueId)!!.currentGame = game.id
		player.sendMessage("§a" + messages.get("chat-game-join").replace("%d", game.id.toString() + ""))
	}

	override fun onEnable() {
		instance = this

		getLogger().info("Copyright (C) 2023 FALLET Nathan\n\n"
				+ "This program is free software; you can redistribute it and/or modify\n"
				+ "it under the terms of the GNU General Public License as published by\n"
				+ "the Free Software Foundation; either version 2 of the License, or\n"
				+ "(at your option) any later version.\n\n"
				+ "This program is distributed in the hope that it will be useful,\n"
				+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n"
				+ "GNU General Public License for more details.\n\n"
				+ "You should have received a copy of the GNU General Public License along\n"
				+ "with this program; if not, write to the Free Software Foundation, Inc.,\n"
				+ "51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.")

		saveDefaultConfig()
		reloadConfig()

		val messagesFile = File(getDataFolder(), "messages.yml")
		if (!messagesFile.exists()) {
			saveResource("messages.yml", false)
		}
		val msgs = YamlConfiguration.loadConfiguration(messagesFile)
		msgs.getKeys(false).forEach { id ->
			messages.set(id, msgs.getString(id) ?: "")
		}

		val worldCreator = WorldCreator("Replica")
		worldCreator.type(WorldType.FLAT)
		worldCreator.generator(ReplicaGenerator())
		worldCreator.createWorld()
		val world = Bukkit.getWorld("Replica")
		world?.setDifficulty(Difficulty.PEACEFUL)
		world?.setSpawnLocation(-1000, 0, 0)
		world?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
		world?.setTime(0)

		Bukkit.getOnlinePlayers().forEach { p ->
			initPlayer(p)
		}

		games.clear()
		val gamesAmount = getConfig().getInt("games-amount")
		var i = 1
		val gamesFile = YamlConfiguration.loadConfiguration(File(getDataFolder(), "games.yml"))
		gamesFile.getKeys(false).forEach { key ->
			if (i > gamesAmount) {
				return@forEach
			}
			val game = Game(i)
			val data = gamesFile.getConfigurationSection(key) ?: return@forEach
			data.getKeys(false).forEach { sk ->
				game.signs.add(Location(
					Bukkit.getWorld(data.getString(sk + ".world") ?: Bukkit.getWorlds()[0].name),
					data.getDouble(sk + ".x"),
					data.getDouble(sk + ".y"),
					data.getDouble(sk + ".z")
				))
			}
			games.add(game)
			i++
		}
		while (i <= gamesAmount) {
			games.add(Game(i))
			i++
		}
		if (games.size < 1) {
			getLogger().severe("You have to add one game or more to use this plugin !")
			Bukkit.getPluginManager().disablePlugin(this)
			return
		}
		games.forEach { game ->
			game.loadPlots()
		}

		pictures.clear()
		getConfig().getConfigurationSection("pictures")?.let { pictureFile ->
			pictureFile.getKeys(false).forEach { s ->
				val picture = Picture(pictureFile.getString(s + ".name") ?: "Unknown")
				val blocks = pictureFile.getString(s + ".blocks")?.split(";") ?: return@forEach
				for (x in 0..7) {
					for (y in 0..7) {
						picture.blocks[Pair(x, y)] = Integer.parseInt(blocks[y * 8 + x])
					}
				}
				pictures.add(picture)
			}
		}
		if (pictures.size < 1) {
			getLogger().severe("You have to add one picture or more to use this plugin !")
			Bukkit.getPluginManager().disablePlugin(this)
			return
		}

		val pm = Bukkit.getPluginManager()
		pm.registerEvents(PlayerJoin, this)
		pm.registerEvents(PlayerQuit, this)
		pm.registerEvents(PlayerInteract, this)
		pm.registerEvents(PlayerRespawn, this)
		pm.registerEvents(EntityDamage, this)
		pm.registerEvents(BlockPlace, this)
		pm.registerEvents(BlockBreak, this)
		pm.registerEvents(SignChange, this)
		pm.registerEvents(PlayerCommandPreprocess, this)

		getCommand("replica")?.setExecutor(Cmd)

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Runnable {
			games.forEach { game ->
				val players = game.players
				if (game.state == GameState.inGame) {
					game.verifNext()
				} else {
					if (players.size > 1 && game.state == GameState.waiting) {
						game.state = GameState.startCount
						game.currentCountValue = countdown + 1
					}
					if (game.state == GameState.startCount && players.size < 2) {
						game.state = GameState.waiting
						game.currentCountValue = 0
					}
					if (game.state == GameState.startCount) {
						game.currentCountValue = game.currentCountValue - 1
						if (game.currentCountValue == 0) {
							game.start()
						} else if (
							game.currentCountValue == 60 ||
							game.currentCountValue == 30 ||
							game.currentCountValue == 20 ||
							game.currentCountValue == 10 ||
							game.currentCountValue <= 5
						) {
							players.mapNotNull {
								Bukkit.getPlayer(it)
							}.forEach {
								it.sendMessage("§e" + messages.get("chat-start-count").replace("%d", game.currentCountValue.toString()))
							}
						}
					}
				}
				val lines = listOf(
					"§b",
					"§b§l" + messages.get("sb-players"),
					"§f${players.size}/10",
					"§a",
					"§a§l" + messages.get("sb-status"),
					"§f" + game.state.text?.replace("%d", game.currentCountValue.toString()),
					"§e",
					"§e§lPlugin by Nathan Fallet"
				)
				game.allPlayers.mapNotNull {
					val player = Bukkit.getPlayer(it)
					val zp = getPlayer(it)
					if (player != null && zp != null) Pair(player, zp)
					else null
				}.forEach { pair ->
					pair.second.scoreboard.update(pair.first, lines)
				}
				game.updateSigns()
			}
			players.forEach { zp ->
				if (zp.currentGame == 0 && zp.scoreboard.active) {
					zp.scoreboard.kill()
				}
			}
		}, 0, 20)

		val metrics = Metrics(this, 800)
		metrics.addCustomChart(SimplePie("pictures_count", Callable<String> {
			"${pictures.size.toString()} picture${if (pictures.size > 1) "s" else ""}"
		}))
		metrics.addCustomChart(SimplePie("games_count", Callable<String> {
			"${games.size.toString()} game${if (games.size > 1) "s" else ""}"
		}))
	}

	override fun onDisable() {
		games.forEach { game ->
			game.allPlayers.mapNotNull {
				Bukkit.getPlayer(it)
			}.forEach { player ->
				player.sendMessage("§c" + messages.get("reload-msg"))
				getConfig().getString("spawn-command")?.let { command ->
					Bukkit.dispatchCommand(player, command)
				}
				player.gameMode = GameMode.SURVIVAL
				player.inventory.clear()
				player.updateInventory()
			}
			game.loadPlots()
		}
		players.clear()
		val gamesFile = YamlConfiguration.loadConfiguration(File("null"))
		games.forEach { game ->
			var i = 1
			game.signs.forEach { location ->
				gamesFile.set("game" + game.id + ".sign" + i + ".world", location.world?.name)
				gamesFile.set("game" + game.id + ".sign" + i + ".x", location.blockX)
				gamesFile.set("game" + game.id + ".sign" + i + ".y", location.blockY)
				gamesFile.set("game" + game.id + ".sign" + i + ".z", location.blockZ)
				i++
			}
		}
		try {
			gamesFile.save(File(getDataFolder(), "games.yml"))
		} catch (e: IOException) {
			e.printStackTrace()
		}
		games.clear()
	}

}