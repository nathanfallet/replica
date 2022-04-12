/*
 *  Copyright (C) 2020 FALLET Nathan
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

package me.nathanfallet.replica.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.replica.Replica;
import me.nathanfallet.replica.utils.ZabriPlayer;

public class Cmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Check that args are specified
		if (args.length != 0) {
			// Go to command
			if (args[0].equalsIgnoreCase("goto")) {
				// Permission check
				if (sender.hasPermission("replica.admin")) {
					// Convert to player
					if (sender instanceof Player) {
						// Teleport
						Player p = (Player) sender;
						p.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-goto-success"));
						p.teleport(new Location(Bukkit.getWorld("Replica"), 3.5, 65, 4.5));
					} else {
						// Error
						sender.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-error-not-a-player"));
					}
				} else {
					// Error
					sender.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-error-perm"));
				}
			}
			// Build mode command
			else if (args[0].equalsIgnoreCase("buildmode")) {
				// Permission check
				if (sender.hasPermission("replica.admin")) {
					// Convert to player
					if (sender instanceof Player) {
						Player p = (Player) sender;
						ZabriPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
						if (zp != null) {
							// Set buildmode
							zp.setBuildmode(!zp.isBuildmode());
							p.sendMessage("§c" + Replica.getInstance().getMessages().get(zp.isBuildmode() ? "cmd-buildmode-enable" : "cmd-buildmode-disable"));
						}
					} else {
						// Error
						sender.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-error-not-a-player"));
					}
				} else {
					// Error
					sender.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-error-perm"));
				}
			}
			// Leave command
			else if (args[0].equalsIgnoreCase("leave")) {
				// Convert to player
				if (sender instanceof Player) {
					Player p = (Player) sender;
					ZabriPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
					if (zp.getCurrentGame() != 0) {
						// Leave game
						zp.setCurrentGame(0);
						zp.setPlaying(false);
						zp.setFinish(false);
						zp.setPlot(0);
						Bukkit.dispatchCommand(p, Replica.getInstance().getConfig().getString("spawn-command"));
						p.setGameMode(GameMode.SURVIVAL);
						p.getInventory().clear();
						p.updateInventory();
					} else {
						// Error
						p.sendMessage("§c" + Replica.getInstance().getMessages().get("chat-no-game"));
					}
				} else {
					// Error
					sender.sendMessage("§c" + Replica.getInstance().getMessages().get("cmd-error-not-a-player"));
				}
			}
			// Command not found, send help
			else {
				sendHelp(sender, label);
			}
		} else {
			sendHelp(sender, label);
		}
		return true;
	}

	public void sendHelp(CommandSender sender, String label) {
		if (sender.hasPermission("replica.admin")) {
			sender.sendMessage("§e/" + label + " goto : " + Replica.getInstance().getMessages().get("cmd-help-goto")
					+ "\n" + "§e/" + label + " buildmode : "
					+ Replica.getInstance().getMessages().get("cmd-help-buildmode") + "\n");
		}
		sender.sendMessage("§e/" + label + " leave : " + Replica.getInstance().getMessages().get("cmd-help-leave"));
	}

}
