package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public class TestPrompt extends ValidatingPrompt {

	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	Connection c = null;
	
	@Override
	public String getPromptText(ConversationContext arg0) {
		if(arg0.getSessionData("quest") != null) {
			String questIntro = arg0.getSessionData("quest").toString();
			questIntro = HollowRPG.textColor(questIntro);
			
			arg0.setSessionData("quest", null);
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + questIntro;
		}
		return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "Sorry, I don't understand?";
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1) {
		
		Player player = Bukkit.getPlayer((String) arg0.getSessionData("player_name"));
		
		//
		// Objective Type #1 (Teleport Player)
		//
		if(arg0.getSessionData("objective_type").toString().equalsIgnoreCase("0")) {
			if(arg1.toString().equalsIgnoreCase("travel")) {
				World world = player.getWorld();
				int x = (Integer) arg0.getSessionData("x");
				int y = (Integer) arg0.getSessionData("y");
				int z = (Integer) arg0.getSessionData("z");
                Location location = new Location(world,x,y,z);
				player.teleport(location);
				
				return END_OF_CONVERSATION;
				
			}
		}
		
		//
		// Objective Type #2 (Kill / Collect Quest)
		//
		if(arg0.getSessionData("objective_type").toString().equalsIgnoreCase("1") || arg0.getSessionData("objective_type").toString().equalsIgnoreCase("2")) {
			if(arg1.toString().equalsIgnoreCase("accept")) {
				c = plugin.myconn.open();
				arg0.setSessionData("result", "1");
				Statement statement = null;
				
				try {
					statement = c.createStatement();
					
					statement.executeUpdate("INSERT INTO active_quests (player_name, quest_id, counter) VALUES ('" + arg0.getSessionData("player_name") + "'," + arg0.getSessionData("quest_id") + ",0)");
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return END_OF_CONVERSATION;
			}
		}
		
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1) {
		return true;
	}

}
