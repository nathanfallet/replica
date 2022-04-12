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

package me.nathanfallet.replica.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class PlayerScoreboard {

	private Objective objective;
	private ArrayList<String> lastLines = new ArrayList<String>();
	private String name;

	public PlayerScoreboard(String name) {
		this.name = name;
	}

	public Objective getObjective() {
		return objective;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return (objective != null);
	}

	public void update(Player player, ArrayList<String> newLines) {
		if (objective == null) {
			objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective(name.toLowerCase(),
					"dummy", name);
			objective.setDisplayName("§6§l" + name);
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		for (int pos = 0; pos < newLines.size(); pos++) {
			if (lastLines != null && lastLines.size() > pos && lastLines.get(pos) != null
					&& !lastLines.get(pos).equals(newLines.get(pos))) {
				objective.getScoreboard().resetScores(lastLines.get(pos));
			}
			objective.getScore(newLines.get(pos)).setScore(newLines.size() - pos);
		}
		lastLines = newLines;
		player.setScoreboard(objective.getScoreboard());
	}

	public void kill() {
		objective.unregister();
		objective = null;
		lastLines.clear();
	}

}
