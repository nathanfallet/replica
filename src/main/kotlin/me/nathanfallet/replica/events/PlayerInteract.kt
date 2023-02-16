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

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.Game

class PlayerInteract: Listener {

	@EventHandler
	fun onPLayerInteract(e: PlayerInteractEvent) {
		if (e.action == Action.RIGHT_CLICK_BLOCK) {
			if (e.clickedBlock?.type == Material.OAK_SIGN) {
				Replica.instance?.games?.forEach { game ->
					game.signs.forEach { location ->
						if (e.clickedBlock?.location == location) {
							Replica.instance?.joinPlayer(e.player, game)
						}
					}
				}
			}
		}
	}

}
