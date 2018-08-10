package steven.Sync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EventsClass implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				getInfo(player);
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("VitalsSQLSync"), 60);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		saveInfo(player);
	}
	
	public void getInfo(Player player) {
		player.sendMessage(ChatColor.YELLOW + "Getting data" + getMainGroup(player) + " " + getSubGroup(player));
		if (playerExists(player)) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				
				double bal = plugin.economy.getBalance(player);
				plugin.economy.withdrawPlayer(player, bal);
				plugin.economy.depositPlayer((OfflinePlayer)player, results.getDouble("BALANCE"));
				for (String g : plugin.permission.getPlayerGroups(player)) {
					plugin.permission.playerRemoveGroup(player, g);
				}
				plugin.permission.playerAddGroup(player, results.getString("RANK"));
				plugin.permission.playerAddGroup(player, results.getString("SUB"));
				plugin.getPlaytime().set(player.getUniqueId().toString() + ".time", results.getInt("PLAYTIME"));
				plugin.savePlaytime();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			createPlayer(player.getUniqueId(), player);
	}

	public boolean playerExists(Player player) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID = ?");
			statement.setString(1, player.getUniqueId().toString());
			ResultSet results = statement.executeQuery();
			boolean hasData = results.next();
			if (hasData)
				return true;
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveInfo(Player player) {
		String playerMain = getMainGroup(player), playerSub = getSubGroup(player);
		if (playerExists(player)) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				int playtime = plugin.getPlaytime().getInt(player.getUniqueId().toString() + ".time");
				
				PreparedStatement insert = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET BALANCE = ?, PLAYTIME = ?, RANK = ?, SUB = ? WHERE UUID = ?");
				insert.setDouble(1, plugin.economy.getBalance(player));
				insert.setInt(2, playtime++);
				//Bukkit.getConsoleSender().sendMessage("" + playerMain + " " + playerSub);
				insert.setString(3, playerMain);
				insert.setString(4, playerSub);
				insert.setString(5, player.getUniqueId().toString());
				insert.executeUpdate();
				insert.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			createPlayer(player.getUniqueId(), player);
	}
	
	private String getSubGroup(Player player) {
		if (player.hasPermission("owner.v")) {
			return "Owner";
		}
		else if (player.hasPermission("admin.v")) {
			return "Admin";
		}
		else if (player.hasPermission("mod.v")) {
			return "Mod";
		}
		else if (player.hasPermission("trusted.v")) {
			return "Trusted";
		}
		else if (player.hasPermission("sapphire.v")) {
			return "Sapphire";
		}
		else if (player.hasPermission("ruby.v")) {
			return "Ruby";
		}
		else if (player.hasPermission("drangonstone.v")) {
			return "Dragonstone";
		}
		else if (player.hasPermission("veteran.v")) {
			return "Veteran";
		}
		else
			return "";
	}

	private String getMainGroup(Player player) {
		if (player.hasPermission("wanderer.v")) {
			return "Wanderer";
		}
		else if (player.hasPermission("citizen.v")) {
			return "Citizen";
		}
		else if (player.hasPermission("noble.v")) {
			return "Noble";
		}
		else if (player.hasPermission("merchant.v")) {
			return "Merchant";
		}
		else if (player.hasPermission("knight.v")) {
			return "Knight";
		}
		else if (player.hasPermission("baron.v")) {
			return "Baron";
		}
		else if (player.hasPermission("duke.v")) {
			return "Duke";
		}
		else if (player.hasPermission("chancellor.v")) {
			return "Chancellor";
		}
		else if (player.hasPermission("viceroy.v")) {
			return "Viceroy";
		}
		else if (player.hasPermission("guardian.v")) {
			return "Guardian";
		}
		else if (player.hasPermission("avatar.v")) {
			return "Avatar";
		}
		else
			return "Unknown";
	}

	public void createPlayer(final UUID uuid, Player player) {
		String playerMain = "Wanderer", playerSub = "";
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			System.out.print(1);
			if (!playerExists(player)) {
				PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID,BALANCE,PLAYTIME,RANK,SUB) VALUES (?,?,?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setDouble(2, plugin.economy.getBalance(player));
				insert.setInt(3, 0);
				for (String s : plugin.permission.getPlayerGroups(player)) {
					List<String> main = plugin.getConfig().getStringList("MainGroups");
					for (int i = 0; i < main.size(); i++)
						main.set(i, main.get(i).toLowerCase());
					List<String> sub = plugin.getConfig().getStringList("SubGroups");
					if (main.contains(s))
						playerMain = s;
					else if (sub.contains(s))
						playerSub = s;
				}
				insert.setString(4, playerMain);
				insert.setString(5, playerSub);
				insert.executeUpdate();
				insert.close();

				plugin.getServer().broadcastMessage(ChatColor.GREEN + "Player Created");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
