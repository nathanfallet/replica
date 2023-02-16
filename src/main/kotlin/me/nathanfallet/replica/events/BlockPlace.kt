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

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

import me.nathanfallet.replica.Replica
import me.nathanfallet.replica.models.Game
import me.nathanfallet.replica.models.ZabriPlayer
import me.nathanfallet.replica.models.GameState

class BlockPlace: Listener {

	@EventHandler(ignoreCancelled = true)
	fun onBlockBreak(e: BlockPlaceEvent) {
		if (e.block.world.name == "Replica") {
			val zp = Replica.instance?.getPlayer(e.player.uniqueId)
			if (zp != null && zp.buildmode) {
				e.setCancelled(false)
				return
			}
			if (e.block.location.blockY != 64) {
				e.setCancelled(true)
				return
			}
			if (e.block.location.blockZ < 0 || e.block.location.blockZ > 320) {
				e.setCancelled(true)
				return
			}
			if (!e.block.type.toString().endsWith("_TERRACOTTA")) {
				e.setCancelled(true)
				return
			}
			var z = e.block.chunk.z
            var col = 0
			while (z >= 2) {
				z -= 2
				col++
			}
			col++
			Replica.instance?.games?.forEach { game ->
				game.players.forEach { uuid ->
					if (e.player.uniqueId == uuid) {
						if (game.state == GameState.inGame) {
							if (zp?.plot == col) {
								e.setCancelled(false)
								if (game.isCompletingPlot(col)) {
									game.breakPlot(col)
									e.player.inventory.clear()
									e.player.updateInventory()
									e.player.gameMode = GameMode.SPECTATOR
									zp.finish = true
									game.verifNext()
								}
							} else {
								e.setCancelled(true)
							}
						} else {
							e.setCancelled(true)
						}
						return
					}
				}
			}
			e.setCancelled(true)
		}
	}

}
