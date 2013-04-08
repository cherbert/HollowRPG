package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

//This is your trait that will be applied to a npc using the /trait mytraitname command. Each NPC gets its own instance of this class.
public class HollowTrait extends Trait {

	MySQL MySQL = new MySQL("localhost", "3306", "hollowrpg", "minecraft", "");
	Connection c = null;
	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	
	public static HashMap<String, Integer> IsConvo = new HashMap<String, Integer>();
	
	private ConversationFactory factory;
	
	public HollowTrait() {
		super("hollowrpg");

	}
	
	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
		final Player player = event.getClicker();
		
		if (IsConvo.containsKey(player.getName())) {
			int clicked = IsConvo.get(player.getName());
			plugin.getServer().getLogger().info("" + clicked);
				if(clicked == 1) {
					return;
				}
		}
		this.factory = new ConversationFactory(plugin);
		
		if(event.getNPC() == this.getNPC()) {
			String npc_name = event.getNPC().getFullName();
			Bukkit.dispatchCommand(player, "npc select");
			IsConvo.put(player.getName(), 1);
			c = MySQL.open();
			
			try {
				Statement statement = c.createStatement();
				ResultSet res;
				res = statement.executeQuery("SELECT * FROM npcs LEFT JOIN quests ON quests.npc_id = npcs.id WHERE npc_name = '" + npc_name + "'");
				res.next();
				if(res.getRow() == 0) {
					player.sendMessage("Greetings! I can find no quests in the database.");
					
				} else {
					NPC npc;
					npc =	((Citizens)	Bukkit.getServer().getPluginManager().getPlugin("Citizens")).getNPCSelector().getSelected(player);
					if(npc != null ){
						player.sendMessage("");
						player.sendMessage(res.getString("greeting_text"));
						player.sendMessage(ChatColor.YELLOW + "You are now in NPC Chat mode. To exit type " + ChatColor.WHITE + "exit");
						player.sendMessage("");
						
						
						Map<Object, Object> map = new HashMap<Object, Object>();
						
						map.put("npc_name", res.getString("npc_name"));
						map.put("quest",ChatColor.WHITE + res.getString("quest_detail"));
						
						Conversation conv = factory.withFirstPrompt(new TestPrompt()).withInitialSessionData(map).withPrefix(new ConversationPrefix(){
							@Override
							public String getPrefix(ConversationContext context)
							{
								return ChatColor.BLUE + "[" + ChatColor.AQUA + "NPC Chat" + ChatColor.BLUE + "] " + ChatColor.WHITE;
							}
						}).withEscapeSequence("/exit").buildConversation(Bukkit.getPlayer(player.getName()));
						
						conv.addConversationAbandonedListener(new ConversationAbandonedListener() {
							 
					        @Override
							public void conversationAbandoned(ConversationAbandonedEvent arg0) {
					        	IsConvo.put(player.getName(),0);
								plugin.getServer().getLogger().info("hello");
								player.sendMessage("");
								player.sendMessage(ChatColor.DARK_AQUA + "Goodbye!");
							}
					    });
						conv.begin();
						
					}
					else{
						player.sendMessage(ChatColor.RED + "You must have a NPC selected to use this command");
						return;
					}	
					return;
				}
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
    // Called every tick
    @Override
    public void run() {
    }

	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info(npc.getName() + " has been assigned the HollowRPG Trait!");
	}

	@Override
	public void onDespawn() {
    }

	@Override
	public void onSpawn() {

	}

	@Override
	public void onRemove() {
	}

}