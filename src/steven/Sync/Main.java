package steven.Sync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	public Commands commands = new Commands();
	public Economy economy = null;
	public ConfigManager cfgm;
	public PlayerInfo pInfo;
	public Manager man;
	public Connection connection;
	public String host, database, username, password, table = "data";
	public int port;
	private int Ticks = 0;
	
	public void onEnable() {
		//getCommand(commands.cmd1).setExecutor(commands);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "MySQL sync is enabled!");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		setupSQL();
		loadConfigManager();
		setupEconomy();
	}
	
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "MySQL sync has be disabled!");		
	}
	
	public void loadConfigManager() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		/*cfgm = new ConfigManager();
		cfgm.setupPlayersGuilds();
		cfgm.savePlayersGuilds();
		cfgm.reloadPlayersGuilds();
		cfgm.setupGuildItem();
		cfgm.saveGuildItems();
		cfgm.reloadGuildItems();*/
	}
	
	public void playerInfo() {
		pInfo = new PlayerInfo();
	}
	
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
					pInfo.playtime();
				}
				/*if (Ticks % 39 == 0) {	//every 1.9 seconds
					utils.emmyRemove();
				}
				if (Ticks % 200 == 0) { //every 10 seconds
					if (getConfig().getBoolean("FeatherFly"))
						utils.featherRemove();
				}*/
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
}
