package steven.Sync;

import org.bukkit.event.Listener;

public class ConfigManager implements Listener {
	
	/*private Main plugin = Main.getPlugin(Main.class);
		
	//files and configs here
	public FileConfiguration playerTimeData;
	public File playerTime;
	
	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		playerTime = new File(plugin.getDataFolder(), "playerTime.yml");
		
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
	}*/
}
