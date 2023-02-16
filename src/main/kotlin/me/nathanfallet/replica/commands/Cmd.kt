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

package me.nathanfallet.replica.commands

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.ZabriPlayer

object Cmd: CommandExecutor {

	override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
		// Check that args are specified
		if (args.size == 0) {
			sendHelp(sender, label)
			return true
		}
		
		// Go to command
		if (args[0].equals("goto", ignoreCase = true)) {
			// Permission check
			if (!sender.hasPermission("replica.admin")) {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-error-perm"))
				return true
			}
			// Convert to player
			if (sender !is Player) {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-error-not-a-player"))
				return true
			}
			sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-goto-success"))
			sender.teleport(Location(Bukkit.getWorld("Replica"), 3.5, 65.0, 4.5))
			return true
		}
		// Build mode command
		if (args[0].equals("buildmode", ignoreCase = true)) {
			// Permission check
			if (!sender.hasPermission("replica.admin")) {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-error-perm"))
				return true
			}
			// Convert to player
			if (sender !is Player) {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-error-not-a-player"))
				return true
			}
			Replica.instance?.getPlayer(sender.getUniqueId())?.let { zp ->
				// Set buildmode
				zp.buildmode = !zp.buildmode
				sender.sendMessage("§c" + Replica.instance?.messages?.get(
					if (zp.buildmode) "cmd-buildmode-enable"
					else "cmd-buildmode-disable"
				))
			}
			return true
		}
		// Leave command
		if (args[0].equals("leave", ignoreCase = true)) {
			// Convert to player
			if (sender !is Player) {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("cmd-error-not-a-player"))
				return true
			}
			val zp = Replica.instance?.getPlayer(sender.getUniqueId())?.takeIf {
				it.currentGame != 0
			} ?: run {
				sender.sendMessage("§c" + Replica.instance?.messages?.get("chat-no-game"))
				return true
			}
			// Leave game
			zp.currentGame = 0
			zp.playing = false
			zp.finished = false
			zp.plot = 0
			Replica.instance?.getConfig()?.getString("spawn-command")?.let { command ->
                Bukkit.dispatchCommand(sender, command)
            }
			sender.gameMode = GameMode.SURVIVAL
			sender.inventory.clear()
			sender.updateInventory()
			return true
		}

		// Command not found, send help
		sendHelp(sender, label)
		return true
	}

	fun sendHelp(sender: CommandSender, label: String) {
		if (sender.hasPermission("replica.admin")) {
			sender.sendMessage(
				"§e/" + label + " goto : " + Replica.instance?.messages?.get("cmd-help-goto") + "\n" +
				"§e/" + label + " buildmode : " + Replica.instance?.messages?.get("cmd-help-buildmode") + "\n"
			)
		}
		sender.sendMessage("§e/" + label + " leave : " + Replica.instance?.messages?.get("cmd-help-leave"))
	}

}
