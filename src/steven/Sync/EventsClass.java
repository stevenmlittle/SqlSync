package steven.Sync;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventsClass implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		plugin.pInfo.getInfo(player);		
	}
	
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		plugin.pInfo.saveInfo(player);
	}

}
