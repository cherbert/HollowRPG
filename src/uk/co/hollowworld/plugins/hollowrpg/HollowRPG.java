package uk.co.hollowworld.plugins.hollowrpg;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class HollowRPG extends JavaPlugin{
	
	public MySQL myconn = new MySQL("localhost", "3306", "hollowrpg", "minecraft", "");
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new HollowRPGListener(), this);
		getCommand("hollow").setExecutor(new HollowRPGCommands());
		
		//NPCRegistry registry = CitizensAPI.getNPCRegistry();
		
		//NPC npc = registry.createNPC(EntityType.PLAYER, "cubeydoom");
		
		//npc.spawn(Bukkit.getWorlds().get(0).getSpawnLocation());
		//check if Citizens is present and enabled.

		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	

		//Register your trait with Citizens.        
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(HollowTrait.class).withName("hollowrpg"));
		//npc.addTrait(HollowTrait.class);
		
		
		
		
	}

}
