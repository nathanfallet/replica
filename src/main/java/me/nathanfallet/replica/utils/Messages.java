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

import java.util.HashMap;

import org.bukkit.ChatColor;

public class Messages {

	private HashMap<String, String> messages = new HashMap<String, String>();

	public void set(String id, String msg) {
		if (messages.containsKey(id.toLowerCase())) {
			messages.replace(id.toLowerCase(), ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			messages.put(id.toLowerCase(), ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	public String get(String id) {
		if (messages.containsKey(id.toLowerCase())) {
			return messages.get(id.toLowerCase());
		}
		return "Unknow message : " + id.toLowerCase() + " !";
	}

}
