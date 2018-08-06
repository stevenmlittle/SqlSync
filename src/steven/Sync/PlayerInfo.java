package steven.Sync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PlayerInfo implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);

	public void getInfo(Player player) {
		if (playerExists(player.getUniqueId())) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				
				plugin.economy.bankWithdraw(player.toString(), plugin.economy.getBalance(player));
				plugin.economy.bankDeposit(player.toString(), results.getDouble("BALANCE"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			createPlayer(player.getUniqueId(), player);
		
	}

	public boolean playerExists(UUID player) {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
			statement.setString(1, player.toString());

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Player Found");
				return true;
			}
			plugin.getServer().broadcastMessage(ChatColor.RED + "Player NOT Found");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void saveInfo(Player player) {
		if (playerExists(player.getUniqueId())) {
			try {
				PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
				statement.setString(1, player.getUniqueId().toString());
				ResultSet results = statement.executeQuery();
				results.next();
				int playtime = results.getInt("PLAYTIME");
				
				PreparedStatement insert = plugin.getConnection().prepareStatement("REPLACE INTO " + plugin.table + " (UUID,BALANCE,PLAYTIME) VALUES (?,?,?)");
				insert.setString(1, results.getString("UUID"));
				insert.setDouble(2, results.getDouble("BALANCE"));
				insert.setInt(3, playtime);
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
			if (playerExists(uuid) != true) {
				PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.table + " (UUID,BALANCE,PLAYTIME) VALUES (?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setDouble(2, plugin.economy.getBalance(player));
				insert.setInt(3, 0);
				insert.executeUpdate();

				plugin.getServer().broadcastMessage(ChatColor.GREEN + "Player Created");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void playtime() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (playerExists(player.getUniqueId())) {
				try {
					PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
					statement.setString(1, player.getUniqueId().toString());
					ResultSet results = statement.executeQuery();
					results.next();
					int playtime = results.getInt("PLAYTIME");
					playtime++;
					
					PreparedStatement insert = plugin.getConnection().prepareStatement("REPLACE INTO " + plugin.table + " (UUID,BALANCE,PLAYTIME) VALUES (?,?,?)");
					insert.setString(1, results.getString("UUID"));
					insert.setDouble(2, results.getDouble("BALANCE"));
					insert.setInt(3, playtime);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
