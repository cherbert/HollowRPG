package uk.co.hollowworld.plugins.hollowrpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;



public class TestPrompt extends ValidatingPrompt {

	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");

	@Override
	public String getPromptText(ConversationContext arg0) {
		if(arg0.getSessionData("quest") != null) {
			String questIntro = arg0.getSessionData("quest").toString();
			arg0.setSessionData("quest", null);
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + arg0.getSessionData("npc_name") + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + questIntro;
		} else {
			return "" + arg0.getSessionData("selected_option");
		}
		
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1) {
		arg0.setSessionData("selected_option", arg1);
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1) {
		return true;
	}

}
