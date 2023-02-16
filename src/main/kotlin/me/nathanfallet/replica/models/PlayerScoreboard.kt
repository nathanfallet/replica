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

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective

data class PlayerScoreboard(
    var objective: Objective?,
    var lastLines: List<String>,
    val name: String
) {

    constructor(name: String) : this(null, listOf(), name)
    
    val active: Boolean
        get() = objective != null

	fun update(player: Player, newLines: List<String>) {
        if (objective == null) {
            objective = Bukkit.getScoreboardManager()?.newScoreboard?.registerNewObjective(
                name.lowercase(),
                Criteria.DUMMY,
                name
            )
            objective?.displayName = "§6§l" + name
            objective?.displaySlot = DisplaySlot.SIDEBAR
        }
        for (pos in 0 until newLines.size) {
            if (lastLines.size > pos && lastLines[pos] != newLines[pos]) {
                objective?.scoreboard?.resetScores(lastLines[pos])
            }
            objective?.getScore(newLines[pos])?.score = newLines.size - pos
        }
        lastLines = newLines
        objective?.scoreboard?.let {
            player.scoreboard = it
        }
    }

    fun kill() {
        objective?.unregister()
        objective = null
        lastLines = listOf()
    }

}
