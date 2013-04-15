package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HollowRPGListener implements Listener {
	
	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		//Player p = event.getPlayer();
		//Block bp = event.getBlockPlaced();
		//p.sendMessage("You placed a " + bp.getType().toString());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player p = event.getPlayer();
		p.sendMessage("...Reticulating Splines...");
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player player = e.getEntity();
			player.setWalkSpeed((float) 0.2);
			HollowTrait.IsConvo.put(player.getName(),0);
		}
	}
	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
			Player player = e.getPlayer();
			player.setWalkSpeed((float) 0.2);
			HollowTrait.IsConvo.put(player.getName(),0);
	}
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent e){
		if(e.getEntity().getKiller() == null){
			return;
		}
		
		String entityKilled = e.getEntityType().toString().toLowerCase();
		Connection c = plugin.myconn.open();
		
		try {
			Statement statement = null;
			Statement statement2 = null;
			statement = c.createStatement();
			statement2 = c.createStatement();
			
			
			ResultSet chk_res = statement.executeQuery("SELECT * FROM active_quests LEFT JOIN quests ON quests.quest_id = active_quests.quest_id WHERE counter < objective_count AND objective_type = 2 AND player_name = '" + e.getEntity().getKiller().getName() + "'");
			while(chk_res.next()) {
		        
				if(chk_res.getRow() > 0){
					if(chk_res.getString("objective_entity").equalsIgnoreCase(entityKilled)) {
						int counter = chk_res.getInt("counter") +1;
						int count = chk_res.getInt("objective_count");
						
						@SuppressWarnings("unused")
						int chk_res2 = statement2.executeUpdate("UPDATE active_quests SET counter = counter + 1 WHERE player_name = '" + e.getEntity().getKiller().getName() + "' AND quest_id = " + chk_res.getInt("quest_id"));
						
						String message = " " + Integer.toString(counter) + "/" + Integer.toString(count);
											
						e.getEntity().getKiller().sendMessage("Quest: Kill " + entityKilled + message);
						
					}
	            }
		        
			}
			statement.close();
			statement2.close();
			chk_res.close();
			
			c.close();
				
		} catch (SQLException e1) {
			e1.printStackTrace();	
		}	

    }
	
}
