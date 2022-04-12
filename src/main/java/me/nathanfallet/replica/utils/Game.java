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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nathanfallet.replica.Replica;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class Game {

	private int id;
	private GameState state;
	private int currentCountValue;
	private ArrayList<Location> signs;

	public Game(int id) {
		this.id = id;
		state = GameState.WAITING;
		currentCountValue = 0;
		signs = new ArrayList<Location>();
	}

	public int getId() {
		return id;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public int getCurrentCountValue() {
		return currentCountValue;
	}

	public void setCurrentCountValue(int currentCountValue) {
		this.currentCountValue = currentCountValue;
	}

	public ArrayList<Location> getSigns() {
		return signs;
	}

	public void updateSigns() {
		Iterator<Location> i = signs.iterator();
		while (i.hasNext()) {
			Block b = i.next().getBlock();
			if (b != null && b.getType().equals(Material.OAK_SIGN)) {
				Sign s = (Sign) b.getState();
				s.setLine(0, "§4[Replica]");
				s.setLine(1, Replica.getInstance().getMessages().get("sign-line-game").replaceAll("%d", id + ""));
				s.setLine(2, getPlayers().size() + "/10");
				s.setLine(3, state.getText().replaceAll("%d", currentCountValue + ""));
				s.update();
			} else {
				i.remove();
			}
		}
	}

	public ArrayList<UUID> getPlayers() {
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			ZabriPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
			if (zp.getCurrentGame() == getId()
					&& ((!state.equals(GameState.IN_GAME) && !state.equals(GameState.FINISH)) || zp.isPlaying())
					&& !zp.isBuildmode()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

	public ArrayList<UUID> getAllPlayers() {
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			ZabriPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
			if (zp.getCurrentGame() == getId() && !zp.isBuildmode()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

	public Material makeClay(int color) {
		switch (color) {
			case 0:
				return Material.WHITE_TERRACOTTA;
			case 1:
				return Material.ORANGE_TERRACOTTA;
			case 2:
				return Material.MAGENTA_TERRACOTTA;
			case 3:
				return Material.LIGHT_BLUE_TERRACOTTA;
			case 4:
				return Material.YELLOW_TERRACOTTA;
			case 5:
				return Material.LIME_TERRACOTTA;
			case 6:
				return Material.PINK_TERRACOTTA;
			case 7:
				return Material.GRAY_TERRACOTTA;
			case 8:
				return Material.LIGHT_GRAY_TERRACOTTA;
			case 9:
				return Material.CYAN_TERRACOTTA;
			case 10:
				return Material.PURPLE_TERRACOTTA;
			case 11:
				return Material.BLUE_TERRACOTTA;
			case 12:
				return Material.BROWN_TERRACOTTA;
			case 13:
				return Material.GREEN_TERRACOTTA;
			case 14:
				return Material.RED_TERRACOTTA;
			case 15:
				return Material.BLACK_TERRACOTTA;
		}
		return null;
	}

	public void loadPlots() {
		for (int i = 0; i < 20; i++) {
			for (int x = 5; x < 13; x++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("Replica"), x + Replica.DISTANCE * 16 * (id - 1), 64, z + i * 32)
							.getBlock().setType(Material.AIR);
				}
			}
			for (int y = 0; y < 8; y++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (id - 1), 66 + y, z + i * 32)
							.getBlock().setType(Material.AIR);
				}
			}
		}
	}

	public void breakPlot(int col) {
		col--;
		for (int x = 5; x < 13; x++) {
			for (int z = 5; z < 13; z++) {
				new Location(Bukkit.getWorld("Replica"), x + Replica.DISTANCE * 16 * (id - 1), 64, z + col * 32)
						.getBlock().setType(Material.AIR);
			}
		}
	}

	public void drawPlot(int col, Picture p) {
		col--;
		for (int y = 0; y < 8; y++) {
			for (int z = 5; z < 13; z++) {
				Block b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (id - 1), 66 + (7 - y),
						z + col * 32).getBlock();
				b.setType(makeClay(p.getBlock(z - 5, y)));
			}
		}
	}

	public boolean isCompletingPlot(int col) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (id - 1),
						66 + (7 - y), (7 - x) + col * 32 + 5);
				Location b2 = new Location(Bukkit.getWorld("Replica"), 5 + (7 - y) + Replica.DISTANCE * 16 * (id - 1),
						64, (7 - x) + col * 32 + 5);
				if (!b.getBlock().getType().equals(b2.getBlock().getType())) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsColor(int col, int color) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (id - 1),
						66 + (7 - y), (7 - x) + col * 32 + 5);
				if (b.getBlock().getType().equals(makeClay(color))) {
					return true;
				}
			}
		}
		return false;
	}

	public void start() {
		for (UUID uuid : getPlayers()) {
			ZabriPlayer zp = Replica.getInstance().getPlayer(uuid);
			zp.setPlaying(true);
		}
		state = GameState.IN_GAME;
		loadDraw();
	}

	public void stop() {
		if (state.equals(GameState.IN_GAME)) {
			state = GameState.FINISH;
			Player p = Bukkit.getPlayer(getPlayers().get(0));
			if (p != null) {
				Bukkit.broadcastMessage("§7"
						+ Replica.getInstance().getMessages().get("chat-win-public").replaceAll("%s", p.getName()));
				p.getInventory().clear();
				p.updateInventory();
				p.setGameMode(GameMode.SPECTATOR);
				PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
				pc.getTitleActions().write(0, TitleAction.TITLE);
				pc.getChatComponents().write(0, WrappedChatComponent
						.fromText("§a" + Replica.getInstance().getMessages().get("chat-win-private")));
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				for (String cmd : Replica.getInstance().getConfig().getStringList("reward-commands")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							cmd.replaceAll("%player%", p.getName()).replaceAll("%pseudo%", p.getName()));
				}
			}
			setCurrentCountValue(0);
			loadPlots();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Replica.getInstance(), new Runnable() {
				@Override
				public void run() {
					for (UUID uuid : getAllPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						ZabriPlayer zp = Replica.getInstance().getPlayer(uuid);
						zp.setCurrentGame(0);
						zp.setPlaying(false);
						zp.setFinish(false);
						zp.setPlot(0);
						Bukkit.dispatchCommand(player, Replica.getInstance().getConfig().getString("spawn-command"));
						player.setGameMode(GameMode.SURVIVAL);
						player.getInventory().clear();
						player.updateInventory();
					}
					state = GameState.WAITING;
				}
			}, 100);
		}
	}

	public void loadDraw() {
		Picture p = Replica.getInstance().getRandomPicture();
		loadPlots();
		draw(p, getPlayers().size());
		int plot = 1;
		PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
		pc.getTitleActions().write(0, TitleAction.TITLE);
		pc.getChatComponents().write(0, WrappedChatComponent.fromText("§6" + p.getName()));
		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			ZabriPlayer zp = Replica.getInstance().getPlayer(uuid);
			Location l = new Location(Bukkit.getWorld("Replica"), 4 + Replica.DISTANCE * 16 * (id - 1), 65,
					(plot - 1) * 32 + 9);
			l.setYaw(-90);
			player.teleport(l);
			player.setGameMode(GameMode.SURVIVAL);
			zp.setPlaying(true);
			zp.setPlot(plot);
			zp.setFinish(false);
			player.getInventory().clear();
			player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			for (int i = 0; i < 16; i++) {
				if (containsColor(plot, i)) {
					player.getInventory().addItem(new ItemStack(makeClay(i), 64));
				}
			}
			player.updateInventory();
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			plot++;
		}
	}

	public void verifNext() {
		int number = getPlayers().size();
		int current = 0;
		UUID no = null;
		for (UUID uuid : getPlayers()) {
			ZabriPlayer zp = Replica.getInstance().getPlayer(uuid);
			if (zp.isFinish()) {
				current++;
			} else {
				no = uuid;
			}
		}
		if (number == 0 || number == 1) {
			stop();
		} else if (current >= number - 1) {
			if (no != null) {
				Player nop = Bukkit.getPlayer(no);
				ZabriPlayer zp = Replica.getInstance().getPlayer(no);
				zp.setPlaying(false);
				zp.setFinish(false);
				zp.setPlot(0);
				for (UUID uuid : getAllPlayers()) {
					Player p = Bukkit.getPlayer(uuid);
					p.sendMessage("§7" + Replica.getInstance().getMessages().get("chat-lose-public").replaceAll("%s",
							nop.getName()));
				}
				nop.getInventory().clear();
				nop.updateInventory();
				nop.setGameMode(GameMode.SPECTATOR);
				PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
				pc.getTitleActions().write(0, TitleAction.TITLE);
				pc.getChatComponents().write(0, WrappedChatComponent
						.fromText("§c" + Replica.getInstance().getMessages().get("chat-lose-private")));
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(nop, pc);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				nop.sendMessage("§7" + Replica.getInstance().getMessages().get("chat-lose-private"));
			}
			if (number == 2) {
				stop();
			} else {
				loadDraw();
			}
		}
	}

	public void draw(Picture p, int limit) {
		for (int i = 1; i <= limit; i++) {
			drawPlot(i, p);
		}
	}

	public enum GameState {

		WAITING("stat-waiting"), START_COUNT("stat-start-count"), IN_GAME("stat-in-game"), FINISH("stat-finished");

		private String name;

		GameState(String name) {
			this.name = name;
		}

		public String getText() {
			return Replica.getInstance().getMessages().get(name);
		}

	}

}
