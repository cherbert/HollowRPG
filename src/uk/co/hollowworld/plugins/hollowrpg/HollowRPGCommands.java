package uk.co.hollowworld.plugins.hollowrpg;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HollowRPGCommands implements CommandExecutor{
		
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("hollow")){
			//sender.sendMessage(args[0]);
			//sender.sendMessage("" + args.length);
			if(args.length == 1){
				sender.sendMessage(args[0]);
				if(args[0].equalsIgnoreCase("version")) {
					sender.sendMessage(ChatColor.YELLOW + "HollowRPG v1.00");
				}
			} else {
				sender.sendMessage("Incorrect number of arguments!");
				return false;
			}	
			
			
			return true;
		}
		
		

						
		
				
		return false;
	}
}
