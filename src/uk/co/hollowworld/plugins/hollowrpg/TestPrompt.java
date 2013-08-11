package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public class TestPrompt extends ValidatingPrompt {

	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	Connection c = null;
	
	@Override
	public String getPromptText(ConversationContext arg0) {
		if(arg0.getSessionData("quest") != null) {
			String questIntro = arg0.getSessionData("quest").toString();
			questIntro = HollowRPG.textColor(questIntro);
			
			arg0.setSessionData("quest", null);
			if(arg0.getSessionData("objective_type").toString().equalsIgnoreCase("0")) {
				return questIntro;
			} else {
				return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + questIntro + "\n\n" + ChatColor.AQUA + "Type " + ChatColor.GRAY + "accept " + ChatColor.AQUA + " to accept this quest or\n" + "Type " + ChatColor.GRAY + "exit " + ChatColor.AQUA + "if you wish to quit NPC Chat.";
			}
		}
		if(arg0.getSessionData("result").toString() == "10") {
			arg0.setSessionData("result", "0");
			//return "test";
			return arg0.getSessionData("destinations").toString();
		}
		if(arg0.getSessionData("result").toString() == "11") {
			arg0.setSessionData("result", "0");
			return "\n\n" + ChatColor.AQUA + arg0.getSessionData("destination").toString() + "\n\n";
		}
				
		return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.AQUA + "Sorry, I don't understand what you are asking me? You can type exit if you want to end your chat with me.\n";
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1) {
		
		//
		// Objective Type #0 (Teleport Player)
		//
		c = plugin.myconn.open();
		
		if(arg0.getSessionData("objective_type").toString().equalsIgnoreCase("0")) {
			//
			// [1] Take me on an Adventure!
			//
			if(arg1.toString().equalsIgnoreCase("1")) {
				
				
				try {
					Statement statement = c.createStatement();
					ResultSet res;
					res = statement.executeQuery("SELECT * FROM destinations ORDER BY RAND() LIMIT 1");
					
					if(res.first()) {
						arg0.setSessionData("result", "12");
						arg0.setSessionData("dest_name", res.getString("destination_name"));
						arg0.setSessionData("destination", res.getString("destination_description"));	
						arg0.setSessionData("x", res.getInt("x"));
						arg0.setSessionData("y", res.getInt("y"));
						arg0.setSessionData("z", res.getInt("z"));
						return END_OF_CONVERSATION;
					} else {
						return this;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//
			// [2] Browse Destinations
			//			
			if(arg1.toString().equalsIgnoreCase("2")) {
				try {
					Statement statement = null;
					statement = c.createStatement();
					ResultSet chk_res = statement.executeQuery("SELECT * FROM destinations WHERE active = 1");
					String tmp = ChatColor.DARK_GREEN + "\n\nPossible Destinations are... " + ChatColor.GOLD + "\n\n";
					while(chk_res.next()) {
						tmp = tmp + chk_res.getString("destination_name") + ", ";
					}
					tmp = tmp.substring(0,tmp.length() -2) + "\n\n" + ChatColor.GRAY + "Type " + ChatColor.WHITE + "info {place} " + ChatColor.GRAY + "or" + ChatColor.WHITE + " go {place}";
					arg0.setSessionData("result", "10");
					arg0.setSessionData("destinations", tmp);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				
				return this;
				
			}
			
			//
			// [3] Default Destination
			//
			if(arg1.toString().equalsIgnoreCase("3")) {
				arg0.setSessionData("result", "12");
				return END_OF_CONVERSATION;	
			}
			
			if(StringUtils.substring(arg1.toString(), 0,4).equalsIgnoreCase("info") && arg1.length() > 5) {
				try {
					Statement statement = null;
					statement = c.createStatement();
					ResultSet chk_res = statement.executeQuery("SELECT * FROM destinations WHERE destination_name = '" + arg1.substring(5).toString() + "'");
					if(chk_res.first()) {
						arg0.setSessionData("dest_name", chk_res.getString("destination_name"));
						arg0.setSessionData("destination", chk_res.getString("destination_description"));	
						arg0.setSessionData("result", "11");
						return this;						
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return this;
				}
			}
			
			if(StringUtils.substring(arg1.toString(),0,2).equalsIgnoreCase("go") && arg1.length() > 3) {
				try {
					Statement statement = c.createStatement();
					
					ResultSet res = statement.executeQuery("SELECT * FROM destinations WHERE destination_name = '" + arg1.substring(3).toString() + "'");
					
					if(res.first()) {
						arg0.setSessionData("result", "12");
						arg0.setSessionData("dest_name", res.getString("destination_name"));
						arg0.setSessionData("destination", res.getString("destination_description"));	
						arg0.setSessionData("x", res.getInt("x"));
						arg0.setSessionData("y", res.getInt("y"));
						arg0.setSessionData("z", res.getInt("z"));
						return END_OF_CONVERSATION;
					} else {
						return this;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//
		// Objective Type #1/2 (Kill / Collect Quest)
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
