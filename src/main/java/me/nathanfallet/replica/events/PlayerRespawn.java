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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.nathanfallet.replica.Replica;
import me.nathanfallet.replica.utils.Game;
import me.nathanfallet.replica.utils.ZabriPlayer;
import me.nathanfallet.replica.utils.Game.GameState;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		ZabriPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp.getCurrentGame() != 0) {
			Game g = null;
			for (Game g2 : Replica.getInstance().getGames()) {
				if (g2.getId() == zp.getCurrentGame()) {
					g = g2;
				}
			}
			if (g != null && g.getState().equals(GameState.IN_GAME)) {
				Location l = new Location(Bukkit.getWorld("Replica"),
						4 + Replica.DISTANCE * 16 * (zp.getCurrentGame() - 1), 65, (zp.getPlot() - 1) * 32 + 9);
				l.setYaw(-90);
				e.setRespawnLocation(l);
			}
		}
	}

}
