package uk.co.hollowworld.plugins.hollowrpg;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HollowRPG extends JavaPlugin{
	
	public MySQL myconn = new MySQL("localhost", "3306", "hollowrpg", "minecraft", "1Wallace");
	
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new HollowRPGListener(), this);
	       
		getCommand("my").setExecutor(new HollowRPGCommands());
		getCommand("destination").setExecutor(new HollowDestinationCommands());
		getCommand("dest").setExecutor(new HollowDestinationCommands());
		
		if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
			getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
			getServer().getPluginManager().disablePlugin(this);	
			return;
		}	
       
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(HollowTrait.class).withName("hollowrpg"));
		
	}
	public static String textColor(String confirm) {
		// TODO Auto-generated method stub
		String tmp = confirm.replace("*YELLOW", "" + ChatColor.YELLOW);
		String tmp2 = tmp.replace("*WHITE", "" + ChatColor.WHITE);
		String tmp3 = tmp2.replace("*RED", "" + ChatColor.RED);
		return tmp3;
	}
	public static String message(String text, CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "[HollowRPG] --== " + ChatColor.LIGHT_PURPLE + " HollowRPG " + ChatColor.GOLD + " ==--");
		sender.sendMessage(ChatColor.GOLD + "[HollowRPG] " + ChatColor.WHITE + text);
	
		return text;
	}

}
