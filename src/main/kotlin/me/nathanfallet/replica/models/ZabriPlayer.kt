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

package me.nathanfallet.replica.models

import java.util.UUID

import org.bukkit.entity.Player

data class ZabriPlayer(
    val uuid: UUID,
    var currentGame: Int,
    var buildmode: Boolean,
    var playing: Boolean,
    var finish: Boolean,
    var plot: Int,
    val scoreboard: PlayerScoreboard
) {

    constructor(p: Player) : this(
        p.uniqueId,
        0,
        false,
        false,
        false,
        0,
        PlayerScoreboard("Replica")
    )

}
