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

package me.nathanfallet.replica.events;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.replica.Replica;
import me.nathanfallet.replica.utils.Game;
import me.nathanfallet.replica.utils.ZabriPlayer;
import me.nathanfallet.replica.utils.Game.GameState;

public class BlockBreak implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equals("Replica")) {
			ZabriPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
			if (zp != null && zp.isBuildmode()) {
				e.setCancelled(false);
				return;
			}
			if (e.getBlock().getLocation().getBlockY() != 64) {
				e.setCancelled(true);
				return;
			}
			if (e.getBlock().getLocation().getBlockZ() < 0 || e.getBlock().getLocation().getBlockZ() > 320) {
				e.setCancelled(true);
				return;
			}
			if (!e.getBlock().getType().toString().endsWith("_TERRACOTTA")) {
				e.setCancelled(true);
				return;
			}
			int z = e.getBlock().getChunk().getZ(), col = 0;
			while (z >= 2) {
				z -= 2;
				col++;
			}
			col++;
			for (Game g : Replica.getInstance().getGames()) {
				for (UUID c : g.getPlayers()) {
					if (e.getPlayer().getUniqueId().equals(c)) {
						if (g.getState().equals(GameState.IN_GAME)) {
							if (zp.getPlot() == col) {
								e.setCancelled(false);
								if (g.isCompletingPlot(col)) {
									g.breakPlot(col);
									e.getPlayer().getInventory().clear();
									e.getPlayer().updateInventory();
									e.getPlayer().setGameMode(GameMode.SPECTATOR);
									zp.setFinish(true);
									g.verifNext();
								}
							} else {
								e.setCancelled(true);
							}
						} else {
							e.setCancelled(true);
						}
						return;
					}
				}
			}
			e.setCancelled(true);
		}
	}

}
