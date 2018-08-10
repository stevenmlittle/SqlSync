package steven.Sync;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	
	public Commands commands = new Commands();
	public Economy economy = null;
	private int minutes = 0;
	public Manager man;
	public Connection connection;
	public String host, database, username, password, table = "data";
	public int port;
	private int Ticks = 0;
	public Permission permission = null;
	
	public void onEnable() {
		//getCommand(commands.cmd1).setExecutor(commands);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL sync is enabled!");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		loadConfigManager();
		setupSQL();
		setupEconomy();
		setupPermissions();
		timer();
	}
	
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "MySQL sync has be disabled!");		
	}
	
	public void loadConfigManager() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		setup();
		getPlaytime();
		savePlaytime();
		reloadPlaytime();
	}
	
	/*public void playerInfo() {
		pInfo = new PlayerInfo();
	}*/
	
	public void Manager() {
		man = new Manager();
	}
	
	public void timer() {
		new BukkitRunnable() {

			@Override
			public void run() {
				
				Ticks++;
				
				/*if (getConfig().getBoolean("World_time")) {
					utils.sunHandler();
					utils.deathTracker(null);
				}
				auct.timer();*/
				
				if (Ticks % 20 == 0) {   //every 1 seconds
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						int time = getPlaytime().getInt(player.getUniqueId().toString() + ".time");
						time++;
						getPlaytime().set(player.getUniqueId().toString() + ".time", time);
					}
					
				}
				/*if (Ticks % 39 == 0) {	//every 1.9 seconds
					utils.emmyRemove();
				}*/
				if (Ticks % 1200 == 0) { //every 1 minute
					minutes++;
					if (minutes == 3) {
						if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
							saveInfo();
							minutes = 0;
							Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Data Saved for " + Bukkit.getServer().getOnlinePlayers().size() + " players");
						}
					}
				}
				else if (Ticks == 2000) { //every 100 seconds and reset timer
					Ticks = 0;
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 1);
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyprovider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (economyprovider != null) {
			economy = economyprovider.getProvider();
		}
		return (economy != null);
	}
	
	public void setupSQL() {
		host = getConfig().getString("host");
		port = getConfig().getInt("port");
		database = getConfig().getString("database");
		username = getConfig().getString("username");
		password = getConfig().getString("password");
		
		try {
			synchronized (this) {
				if (getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void saveInfo() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			String playerMain = getMainGroup(player), playerSub = getSubGroup(player);
			if (playerExists(player)) {
				try {
					PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
					statement.setString(1, player.getUniqueId().toString());
					ResultSet results = statement.executeQuery();
					results.next();
					int playtime = getPlaytime().getInt(player.getUniqueId().toString() + ".time");
					
					PreparedStatement insert = getConnection().prepareStatement("UPDATE " + table + " SET BALANCE = ?, PLAYTIME = ?, RANK = ?, SUB = ? WHERE UUID = ?");
					insert.setDouble(1, economy.getBalance(player));
					insert.setInt(2, playtime++);
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
		}
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
			return "Wanderer";
	}
	
	public boolean playerExists(Player player) {
		try {
			PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID = ?");
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
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	//files and configs here
	public FileConfiguration playerTimeData;
	public File playerTime;
	
	public void setup() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		playerTime = new File(getDataFolder(), "playerTime.yml");
		
		if (!playerTime.exists()) {
			try {
				playerTime.createNewFile();
			}
			catch(IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create the playtime.yml file");
			}
		}
		
		playerTimeData = YamlConfiguration.loadConfiguration(playerTime);
	}
	
	public FileConfiguration getPlaytime() {
		return playerTimeData;
	}
	
	public void savePlaytime() {
		try {
			playerTimeData.save(playerTime);
		}
		catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the playtime.yml file");
		}
	}
	
	public void reloadPlaytime() {
		playerTimeData = YamlConfiguration.loadConfiguration(playerTime);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The playtime.yml file has been reloaded");
	}
}