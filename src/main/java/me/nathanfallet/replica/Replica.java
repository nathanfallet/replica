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

package me.nathanfallet.replica;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.replica.commands.Cmd;
import me.nathanfallet.replica.events.BlockBreak;
import me.nathanfallet.replica.events.BlockPlace;
import me.nathanfallet.replica.events.EntityDamage;
import me.nathanfallet.replica.events.PlayerCommandPreprocess;
import me.nathanfallet.replica.events.PlayerInteract;
import me.nathanfallet.replica.events.PlayerJoin;
import me.nathanfallet.replica.events.PlayerQuit;
import me.nathanfallet.replica.events.PlayerRespawn;
import me.nathanfallet.replica.events.SignChange;
import me.nathanfallet.replica.utils.Game;
import me.nathanfallet.replica.utils.Messages;
import me.nathanfallet.replica.utils.Picture;
import me.nathanfallet.replica.utils.ReplicaGenerator;
import me.nathanfallet.replica.utils.Updater;
import me.nathanfallet.replica.utils.ZabriPlayer;
import me.nathanfallet.replica.utils.Game.GameState;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class Replica extends JavaPlugin {

	public static final int DISTANCE = 5;
	private static Replica instance;

	public static Replica getInstance() {
		return instance;
	}

	private ArrayList<ZabriPlayer> players = new ArrayList<ZabriPlayer>();
	private ArrayList<Picture> pictures = new ArrayList<Picture>();
	private ArrayList<Game> games = new ArrayList<Game>();
	private Messages messages;

	public ZabriPlayer getPlayer(UUID uuid) {
		for (ZabriPlayer current : players) {
			if (current.getUuid().equals(uuid)) {
				return current;
			}
		}
		return null;
	}

	public void initPlayer(Player p) {
		players.add(new ZabriPlayer(p));
	}

	public void uninitPlayer(ZabriPlayer p) {
		if (players.contains(p)) {
			players.remove(p);
		}
	}

	public Picture getRandomPicture() {
		Random r = new Random();
		return pictures.get(r.nextInt(pictures.size()));
	}

	public ArrayList<Game> getGames() {
		return games;
	}

	public int getCountdown() {
		int cd = getConfig().getInt("countdown");
		if (cd < 5) {
			return 30;
		}
		return cd;
	}

	public void joinPlayer(Player p, Game g) {
		if (g.getState().equals(GameState.WAITING) || g.getState().equals(GameState.START_COUNT)) {
			if (g.getPlayers().size() < 10) {
				getPlayer(p.getUniqueId()).setCurrentGame(g.getId());
				p.sendMessage("§a" + messages.get("chat-game-join").replaceAll("%d", g.getId() + ""));
			} else {
				p.sendMessage("§c" + messages.get("chat-game-full"));
			}
		} else {
			p.sendMessage("§c" + messages.get("chat-game-full"));
		}
	}

	public Messages getMessages() {
		return messages;
	}

	public void onEnable() {
		instance = this;

		getLogger().info("Copyright (C) 2020 FALLET Nathan\n\n"
				+ "This program is free software; you can redistribute it and/or modify\n"
				+ "it under the terms of the GNU General Public License as published by\n"
				+ "the Free Software Foundation; either version 2 of the License, or\n"
				+ "(at your option) any later version.\n\n"
				+ "This program is distributed in the hope that it will be useful,\n"
				+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n"
				+ "GNU General Public License for more details.\n\n"
				+ "You should have received a copy of the GNU General Public License along\n"
				+ "with this program; if not, write to the Free Software Foundation, Inc.,\n"
				+ "51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.");

		saveDefaultConfig();
		reloadConfig();

		messages = new Messages();
		File mf = new File(getDataFolder(), "messages.yml");
		if (!mf.exists()) {
			saveResource("messages.yml", false);
		}
		FileConfiguration msgs = YamlConfiguration.loadConfiguration(mf);
		for (String id : msgs.getKeys(false)) {
			messages.set(id, msgs.getString(id));
		}

		WorldCreator w = new WorldCreator("Replica");
		w.type(WorldType.FLAT);
		w.generator(new ReplicaGenerator());
		w.createWorld();
		Bukkit.getWorld("Replica").setDifficulty(Difficulty.PEACEFUL);
		Bukkit.getWorld("Replica").setSpawnLocation(-1000, 0, 0);
		Bukkit.getWorld("Replica").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getWorld("Replica").setTime(0);

		for (Player p : Bukkit.getOnlinePlayers()) {
			initPlayer(p);
		}

		games.clear();
		int i = 1, ga = getConfig().getInt("games-amount");
		FileConfiguration gf = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "games.yml"));
		for (String key : gf.getKeys(false)) {
			if (i <= ga) {
				Game g = new Game(i);
				ConfigurationSection data = gf.getConfigurationSection(key);
				if (data != null) {
					for (String sk : data.getKeys(false)) {
						g.getSigns().add(new Location(Bukkit.getWorld(data.getString(sk + ".world")),
								data.getInt(sk + ".x"), data.getInt(sk + ".y"), data.getInt(sk + ".z")));
					}
				}
				games.add(g);
			}
			i++;
		}
		while (i <= ga) {
			games.add(new Game(i));
			i++;
		}
		if (games.size() < 1) {
			getLogger().severe("You have to add one game or more to use this plugin !");
			getLogger().severe("Vous devez au moins ajoutez une partie pour faire fonctionner le plugin !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		for (Game g : games) {
			g.loadPlots();
		}

		pictures.clear();
		ConfigurationSection pf = getConfig().getConfigurationSection("pictures");
		if (pf != null) {
			for (String s : pf.getKeys(false)) {
				Picture p = new Picture(pf.getString(s + ".name"));
				String[] blocks = pf.getString(s + ".blocks").split(";");
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						p.setBlock(Integer.parseInt(blocks[y * 8 + x]), x, y);
					}
				}
				pictures.add(p);
			}
		}
		if (pictures.size() < 1) {
			getLogger().severe("You have to add one picture or more to use this plugin !");
			getLogger().severe("Vous devez au moins ajoutez une image pour faire fonctionner le plugin !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new PlayerInteract(), this);
		pm.registerEvents(new PlayerRespawn(), this);
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new SignChange(), this);
		pm.registerEvents(new PlayerCommandPreprocess(), this);

		getCommand("replica").setExecutor(new Cmd());

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Game g : games) {
					if (g.getState().equals(GameState.IN_GAME)) {
						g.verifNext();
					} else {
						if (g.getPlayers().size() > 1 && g.getState().equals(GameState.WAITING)) {
							g.setState(GameState.START_COUNT);
							g.setCurrentCountValue(getCountdown() + 1);
						}
						if (g.getState().equals(GameState.START_COUNT) && g.getPlayers().size() < 2) {
							g.setState(GameState.WAITING);
							g.setCurrentCountValue(0);
						}
						if (g.getState().equals(GameState.START_COUNT)) {
							g.setCurrentCountValue(g.getCurrentCountValue() - 1);
							if (g.getCurrentCountValue() == 0) {
								g.start();
							} else if (g.getCurrentCountValue() == 60 || g.getCurrentCountValue() == 30
									|| g.getCurrentCountValue() == 20 || g.getCurrentCountValue() == 10
									|| g.getCurrentCountValue() <= 5) {
								PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
								pc.getTitleActions().write(0, TitleAction.TITLE);
								pc.getChatComponents().write(0, WrappedChatComponent.fromText(
										"§a" + g.getState().getText().replaceAll("%d", g.getCurrentCountValue() + "")));
								for (UUID uuid : g.getPlayers()) {
									Player p = Bukkit.getPlayer(uuid);
									p.sendMessage("§e" + messages.get("chat-start-count").replaceAll("%d",
											g.getCurrentCountValue() + ""));
									try {
										ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
									} catch (InvocationTargetException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					ArrayList<String> lines = new ArrayList<String>();
					lines.add("§b");
					lines.add("§b§l" + messages.get("sb-players"));
					lines.add("§f" + g.getPlayers().size() + "/10");
					lines.add("§a");
					lines.add("§a§l" + messages.get("sb-status"));
					lines.add("§f" + g.getState().getText().replaceAll("%d", g.getCurrentCountValue() + ""));
					lines.add("§e");
					lines.add("§e§lPlugin by Nathan Fallet");
					for (UUID uuid : g.getAllPlayers()) {
						Player p = Bukkit.getPlayer(uuid);
						ZabriPlayer zp = getPlayer(uuid);
						zp.getScoreboard().update(p, lines);
					}
					g.updateSigns();
				}
				for (ZabriPlayer zp : players) {
					if (zp.getCurrentGame() == 0 && zp.getScoreboard().isActive()) {
						zp.getScoreboard().kill();
					}
				}
			}
		}, 0, 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Updater.checkForUpdate(getInstance());
			}
		}, 0, 18000);

		Metrics metrics = new Metrics(this, 800);
		metrics.addCustomChart(new SimplePie("pictures_count", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (pictures.size() + " picture" + (pictures.size() > 1 ? "s" : ""));
			}
		}));
		metrics.addCustomChart(new SimplePie("games_count", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return (games.size() + " game" + (games.size() > 1 ? "s" : ""));
			}
		}));
	}

	public void onDisable() {
		for (Game g : games) {
			for (UUID uuid : g.getAllPlayers()) {
				Player p = Bukkit.getPlayer(uuid);
				p.sendMessage("§c" + messages.get("reload-msg"));
				Bukkit.dispatchCommand(p, getConfig().getString("spawn-command"));
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().clear();
				p.updateInventory();
			}
			g.loadPlots();
		}
		players.clear();
		FileConfiguration gf = YamlConfiguration.loadConfiguration(new File("null"));
		for (Game g : games) {
			int i = 1;
			for (Location l : g.getSigns()) {
				gf.set("game" + g.getId() + ".sign" + i + ".world", l.getWorld().getName());
				gf.set("game" + g.getId() + ".sign" + i + ".x", l.getBlockX());
				gf.set("game" + g.getId() + ".sign" + i + ".y", l.getBlockY());
				gf.set("game" + g.getId() + ".sign" + i + ".z", l.getBlockZ());
				i++;
			}
		}
		try {
			gf.save(new File(getDataFolder(), "games.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		games.clear();
	}

}
