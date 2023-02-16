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

import java.util.UUID

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.Game
import me.nathanfallet.replica.models.ZabriPlayer
import me.nathanfallet.replica.models.GameState

class PlayerCommandPreprocess: Listener {

	@EventHandler
	fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
		val zp = Replica.instance?.getPlayer(e.player.uniqueId)
		if (zp?.currentGame != 0) {
			Replica.instance?.games?.forEach { game ->
				if (game.id == zp?.currentGame && game.state == GameState.inGame) {
					game.allPlayers.forEach { uuid ->
						if (e.player.uniqueId == uuid) {
							if (!e.message.equals("/replica leave", ignoreCase = true)) {
								e.setCancelled(true)
								e.player.sendMessage(
									"§c" + Replica.instance?.messages?.get("cmd-error-only-leave")
                                )
							}
							return
						}
					}
				}
			}
		}
	}

}
