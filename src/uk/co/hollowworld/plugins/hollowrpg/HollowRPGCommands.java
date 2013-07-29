package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HollowRPGCommands implements CommandExecutor{
	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		if(commandLabel.equalsIgnoreCase("my")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("quests")) {
					Connection c = plugin.myconn.open();
					try {
						Statement chk_existing = c.createStatement();
						ResultSet chk_res;
						chk_res = chk_existing.executeQuery("SELECT * FROM active_quests LEFT JOIN quests ON quests.quest_id = active_quests.quest_id WHERE player_name = '" + sender.getName() + "'");
						while(chk_res.next()) {
							sender.sendMessage(chk_res.getString("objective_entity") + " " + chk_res.getString("objective_count"));
							c.close();
							return true;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(args[0].equalsIgnoreCase("destination")) {
					sender.sendMessage("Your destination is currenty set to: " + ChatColor.DARK_AQUA + " Riddleport");
					return true;
				}
				
			}
		}
		return false;
	}
}
