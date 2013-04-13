package uk.co.hollowworld.plugins.hollowrpg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class HollowRPGListener implements Listener {
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		//Player p = event.getPlayer();
		//Block bp = event.getBlockPlaced();
		//p.sendMessage("You placed a " + bp.getType().toString());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player p = event.getPlayer();
		p.sendMessage("HollowQuest Activated");
	}
	
}
