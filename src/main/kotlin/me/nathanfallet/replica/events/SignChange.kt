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
import kotlin.text.toInt

object SignChange: Listener {

	@EventHandler
	fun onSignChange(e: SignChangeEvent) {
		if (e.getLine(0) != "[Replica]") {
			return
		}
		if (!e.player.hasPermission("replica.admin")) {
			e.getPlayer().sendMessage(
                "§c" + Replica.instance?.messages?.get("sign-error-perm")
            )
			e.setCancelled(true)
			return
		}
		val id = e.getLine(1)?.toIntOrNull() ?: run {
			e.getPlayer().sendMessage(
				"§c" + Replica.instance?.messages?.get("sign-error-invalid-line")
			)
			e.setCancelled(true)
			return
		}
		Replica.instance?.games?.find { game ->
			game.id == id
		}?.signs?.add(e.block.location)
	}

}
