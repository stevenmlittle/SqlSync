package steven.Sync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		player.sendMessage(ChatColor.YELLOW + "Getting data");
		if (playerExists(player)) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				
				plugin.economy.bankWithdraw(player.toString(), plugin.economy.getBalance(player));
				plugin.economy.depositPlayer((OfflinePlayer)player, results.getDouble("BALANCE"));
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
		if (playerExists(player)) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				int playtime = results.getInt("PLAYTIME");
				
				PreparedStatement insert = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET BALANCE = ?, PLAYTIME = ? WHERE UUID = ?");
				insert.setDouble(1, plugin.economy.getBalance(player));
				insert.setInt(2, playtime++);
				insert.setString(3, player.getUniqueId().toString());
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
	
	public void createPlayer(final UUID uuid, Player player) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			results.next();
			System.out.print(1);
			if (!playerExists(player)) {
				PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID,BALANCE,PLAYTIME) VALUES (?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setDouble(2, plugin.economy.getBalance(player));
				insert.setInt(3, 0);
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
