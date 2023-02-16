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

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.Game
import me.nathanfallet.replica.models.ZabriPlayer
import me.nathanfallet.replica.models.GameState

object PlayerRespawn: Listener {

	@EventHandler
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		val zp = Replica.instance?.getPlayer(e.player.uniqueId)?.takeIf {
			it.currentGame != 0
		} ?: return
		val game = Replica.instance?.games?.find {
			it.id == zp.currentGame && it.state == GameState.inGame
		} ?: return
		val location = Location(
            Bukkit.getWorld("Replica"),
			(4 + Replica.distance * 16 * (game.id - 1)).toDouble(),
            65.0,
            ((zp.plot - 1) * 32 + 9).toDouble()
        )
		location.yaw = -90f
		e.respawnLocation = location
	}

}
