package uk.co.hollowworld.plugins.hollowrpg;

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

	@Override
	public String getPromptText(ConversationContext arg0) {
		if(arg0.getSessionData("quest") != null) {
			String questIntro = arg0.getSessionData("quest").toString();
			String tmp = questIntro.replace("*YELLOW", "" + ChatColor.YELLOW);
			String tmp2 = tmp.replace("*WHITE", "" + ChatColor.WHITE);
			
			questIntro = tmp2;
			
			arg0.setSessionData("quest", null);
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + questIntro;
		}
		return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "Sorry, I don't understand?";
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1) {
		
		Player player = Bukkit.getPlayer((String) arg0.getSessionData("player_name"));
		
		
		if(arg0.getSessionData("npc_type").toString().equalsIgnoreCase("2")) {
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
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1) {
		return true;
	}

}
