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

package me.nathanfallet.replica.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.Game

class SignChange: Listener {

	@EventHandler
	fun onSignChange(e: SignChangeEvent) {
		if (e.getLine(0).equals("[Replica]")) {
			if (e.player.hasPermission("replica.admin")) {
				try {
					val id = Integer.parseInt(e.getLine(1))
					var result: Game? = null
					Replica.instance?.games?.forEach { game ->
						if (game.id == id) {
							result = game
						}
					}
					if (result == null) {
						throw NumberFormatException()
					}
					result?.signs?.add(e.block.location)
				} catch (ex: NumberFormatException) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(
                        "§c" + Replica.instance?.messages?.get("sign-error-invalid-line")
                    )
				}
			} else {
				e.getPlayer().sendMessage(
                    "§c" + Replica.instance?.messages?.get("sign-error-perm")
                )
			}
		}
	}

}
