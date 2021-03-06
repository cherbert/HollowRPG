package uk.co.hollowworld.plugins.hollowrpg;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HollowRPGListener implements Listener {
	Connection c = null;
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
		p.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to HollowRPG Beta 0.1");
		InetSocketAddress ipAddress = p.getAddress();
		//p.sendMessage("IP Address: " + ipAddress);
		c = plugin.myconn.open();
		Statement statement = null;
		try {
			statement = c.createStatement();
			statement.executeUpdate("DELETE FROM ip WHERE player = '" + p.getName() + "'");
			statement.executeUpdate("INSERT INTO ip (player,ip) VALUES ('" + p.getName() + "','" + ipAddress + "')");
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@EventHandler
    public void onPlayerCreateNPC(PlayerCreateNPCEvent event) {
		NPC citizen = event.getNPC();
		citizen.addTrait(HollowTrait.class);
		event.getCreator().sendMessage("HollowRPG Trait Added.");
	}
	
	@EventHandler
	public void onNPCspawn(NPCSpawnEvent event) {
		NPC citizen = event.getNPC();
		if(citizen.hasTrait(HollowTrait.class)) {
			//citizen.addTrait(HollowTrait.class);
		} else {
			citizen.addTrait(HollowTrait.class);
			
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player p = event.getPlayer();
		c = plugin.myconn.open();
		Statement statement = null;
		try {
			statement = c.createStatement();
			statement.executeUpdate("DELETE FROM ip WHERE player = '" + p.getName() + "'");
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
/*	@EventHandler
    public void onPickup(PlayerPickupItemEvent event){
		ItemStack i = event.getItem().getItemStack();
		@SuppressWarnings("unused")
		int id = i.getTypeId();
		Player p = event.getPlayer();
		
		String itemPicked = i.getType().toString();
		Connection c = plugin.myconn.open();
		
		try {
			Statement statement = null;
			Statement statement2 = null;
			statement = c.createStatement();
			statement2 = c.createStatement();
			String updatestring = null;
			int itemdiff = 0;
			ResultSet chk_res = statement.executeQuery("SELECT * FROM active_quests LEFT JOIN quests ON quests.quest_id = active_quests.quest_id WHERE counter < objective_count AND objective_type = 1 AND player_name = '" + p.getName() + "'");
			while(chk_res.next()) {
		        
				if(chk_res.getRow() > 0){
					if(chk_res.getString("objective_entity").equalsIgnoreCase(itemPicked)) {
						int itemcount = i.getAmount();
						int counter = chk_res.getInt("counter") + itemcount;
						int count = chk_res.getInt("objective_count");
						
						if (counter > count) {
							itemdiff = counter - count;
							counter = count;
							event.getItem().remove();
							updatestring = "counter = " + count;
							
							ItemStack[] item = {new ItemStack(event.getItem().getItemStack().getType(), itemdiff)};
							p.getInventory().addItem(item);
							event.setCancelled(true);
							
						} else {
							event.getItem().remove();
							
							event.setCancelled(true);
							
							updatestring = "counter = counter + " + itemcount;
						}
						
						
						@SuppressWarnings("unused")
						int chk_res2 = statement2.executeUpdate("UPDATE active_quests SET " + updatestring + " WHERE player_name = '" + p.getName() + "' AND quest_id = " + chk_res.getInt("quest_id"));
						
						String message = " " + Integer.toString(counter) + "/" + Integer.toString(count);
											
						p.sendMessage(ChatColor.BLUE + "Quest Update: " + ChatColor.AQUA + "You picked up " + itemPicked + message);
						
						if(count == counter) {
							p.sendMessage(ChatColor.BLUE + "Quest Update: " + ChatColor.AQUA + "Completed! Return to " + chk_res.getString("npc_name") + " for your reward.");	
						}
						
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
    }*/
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
											
						e.getEntity().getKiller().sendMessage(ChatColor.BLUE + "Quest Update: " + ChatColor.AQUA + "Kill " + entityKilled.toUpperCase() + message);
						
						if(count == counter) {
							e.getEntity().getKiller().sendMessage(ChatColor.BLUE + "Quest Update: " + ChatColor.AQUA + "Completed! Return to " + chk_res.getString("npc_name") + " for your reward.");	
						}
						
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
