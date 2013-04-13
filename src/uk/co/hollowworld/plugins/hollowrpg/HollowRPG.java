package uk.co.hollowworld.plugins.hollowrpg;

import java.util.logging.Level;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HollowRPG extends JavaPlugin{
	
	public MySQL myconn = new MySQL("localhost", "3306", "hollowrpg", "minecraft", "1Wallace");
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new HollowRPGListener(), this);
		getCommand("hollow").setExecutor(new HollowRPGCommands());

		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
       
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(HollowTrait.class).withName("hollowrpg"));
		
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player player = e.getEntity();
			player.setWalkSpeed((float) 0.2);
		}
	}
	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
			Player player = e.getPlayer();
			player.setWalkSpeed((float) 0.2);
	}

}
