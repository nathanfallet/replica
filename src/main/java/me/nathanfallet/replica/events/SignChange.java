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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.nathanfallet.replica.Replica;
import me.nathanfallet.replica.utils.Game;

public class SignChange implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[Replica]")) {
			if (e.getPlayer().hasPermission("replica.admin")) {
				try {
					int id = Integer.parseInt(e.getLine(1));
					Game g = null;
					for (Game g2 : Replica.getInstance().getGames()) {
						if (g2.getId() == id) {
							g = g2;
						}
					}
					if (g == null) {
						throw new NumberFormatException();
					}
					g.getSigns().add(e.getBlock().getLocation());
				} catch (NumberFormatException ex) {
					e.setCancelled(true);
					e.getPlayer()
							.sendMessage("§c" + Replica.getInstance().getMessages().get("sign-error-invalid-line"));
				}
			} else {
				e.getPlayer().sendMessage("§c" + Replica.getInstance().getMessages().get("sign-error-perm"));
			}
		}
	}

}
