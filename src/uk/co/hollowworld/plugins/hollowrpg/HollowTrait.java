package uk.co.hollowworld.plugins.hollowrpg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class HollowTrait extends Trait {
	HollowRPG plugin = (HollowRPG) Bukkit.getServer().getPluginManager().getPlugin("HollowRPG");
	
	Connection c = null;
	
	public static HashMap<String, Integer> IsConvo = new HashMap<String, Integer>();
	
	private ConversationFactory factory;
	
	public HollowTrait() {
		super("hollowrpg");

	}
	
	@EventHandler
	public void click(final net.citizensnpcs.api.event.NPCRightClickEvent event){
		final Player player = event.getClicker();
		
		if (IsConvo.containsKey(player.getName())) {
			int clicked = IsConvo.get(player.getName());
				if(clicked == 1) {
					return;
				}
		}
		this.factory = new ConversationFactory(plugin);
		
		if(event.getNPC() == this.getNPC()) {
			
			c = plugin.myconn.open();
			//
			// Kill Quest Check
			//
			try {
				Statement chk_existing = c.createStatement();
				ResultSet chk_res;
				chk_res = chk_existing.executeQuery("SELECT * FROM active_quests LEFT JOIN quests ON quests.quest_id = active_quests.quest_id WHERE npc_name = '" + event.getNPC().getFullName() + "' AND player_name = '" + player.getName() + "'");
				chk_res.next();
				if(chk_res.getRow() > 0) {
					if(chk_res.getInt("objective_type") == 2) {
						if(chk_res.getInt("objective_count") == chk_res.getInt("counter")) {
							if(chk_res.getInt("reward_given") == 1) {
								player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + event.getNPC().getFullName() + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "You have already done my quest.");	
							} else {
								
								//
								// Give item reward
								//
								if(!chk_res.getString("objective_item_reward").equals(null)) {
									
									String item = chk_res.getString("objective_item_reward");
									Integer qty = chk_res.getInt("objective_item_reward_qty");
									
									Material MaterialItem = Material.getMaterial(item);
									ItemStack items = new ItemStack(MaterialItem, qty);
									player.getInventory().addItem(items);
									
								}
							
								//
								// Give money reward
								//
								if(chk_res.getInt("objective_money_reward") > 0) {
									plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "money give "+ player.getName() + " " + chk_res.getInt("objective_money_reward"));
								}
								
								//
								// Send Reward Text to Player
								//
								player.sendMessage("");
								player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + event.getNPC().getFullName() + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + chk_res.getString("objective_text_reward"));
								player.sendMessage("");
								
								//
								// Delete Quest from database if repeatable otherwise update as reward given
								//
								if(chk_res.getInt("is_repeatable") == 1) {
									chk_existing.executeUpdate("DELETE FROM active_quests WHERE quest_id = " + chk_res.getInt("quest_id") + " AND player_name = '" + player.getName() + "'");
								} else {
									int quest_id = chk_res.getInt("quest_id");
									chk_existing.executeUpdate("UPDATE active_quests SET reward_given = 1 WHERE player_name = '" + player.getName() + "' AND quest_id = " + quest_id);
								}
								
								

							}
							
							return;
						}
					}
					
					player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + event.getNPC().getFullName() + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + "You are already doing my quest!" + ChatColor.WHITE);
					
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			}
			
			
			try {
				Statement statement = c.createStatement();
				ResultSet res;
				res = statement.executeQuery("SELECT * FROM quests WHERE npc_name = '" + event.getNPC().getFullName() + "'");
				res.next();
				if(res.getRow() == 0) {
					player.sendMessage("Greetings! I am sorry but I have no quests for you.");
				} else {
					player.setWalkSpeed((float) -1);
					event.getNPC().getNavigator().getDefaultParameters().baseSpeed((float) -1);
					event.getNPC().getNavigator().setTarget(null);
					Bukkit.dispatchCommand(player, "npc select");
					IsConvo.put(player.getName(), 1);
					
					NPC npc;
					npc =	((Citizens)	Bukkit.getServer().getPluginManager().getPlugin("Citizens")).getNPCSelector().getSelected(player);
					if(npc != null ){
						player.sendMessage("");
						player.sendMessage(ChatColor.YELLOW + "You are now in NPC Chat mode.");
						player.sendMessage("");
						
						Map<Object, Object> map = new HashMap<Object, Object>();
						
						map.put("quest_id", res.getString("quest_id"));
						map.put("npc_name", event.getNPC().getFullName());
						map.put("objective_type", res.getInt("objective_type"));

						if(res.getInt("objective_type") == 0) {
							Statement statement2 = c.createStatement();
							ResultSet res2;
							res2 = statement2.executeQuery("SELECT * FROM player_destinations LEFT JOIN destinations ON destinations.destination_name = player_destinations.destination WHERE player = '" + player.getName() + "'");
							res2.first();
							
							if(res2.getRow() > 0) {
								// If no default destination found
								map.put("quest",ChatColor.DARK_GREEN + "Welcome! What distant lands do ya seek?\n\n" + ChatColor.WHITE + "[1]" + ChatColor.GRAY + " Take me on an adventure!\n" + ChatColor.WHITE + "[2]" + ChatColor.GRAY + " Browse Destinations\n" + ChatColor.WHITE + "[3]" + ChatColor.GRAY + " Travel to " + res2.getString("destination") + ChatColor.WHITE + "\n\n" + ChatColor.AQUA + "or Type " + ChatColor.GRAY + "exit " + ChatColor.AQUA + "if you wish to quit NPC Chat.");	
								map.put("dest_name", res2.getString("destination_name"));
								map.put("destination", res2.getString("destination_description"));
								
								map.put("x", res2.getInt("x"));
								map.put("y", res2.getInt("y"));
								map.put("z", res2.getInt("z"));
								
							} else {
								// If default destination found
								map.put("quest",ChatColor.DARK_GREEN + "Welcome! What distant lands do ya seek?\n\n" + ChatColor.WHITE + "[1]" + ChatColor.GRAY + " Take me on an adventure!\n" + ChatColor.WHITE + "[2]" + ChatColor.GRAY + " Browse Destinations" + ChatColor.WHITE+ "\n\n" + ChatColor.AQUA + "or Type " + ChatColor.GRAY + "exit " + ChatColor.AQUA + "if you wish to quit NPC Chat.");
							}
						} else {
							map.put("quest",ChatColor.WHITE + res.getString("quest_detail"));
						}
						
						map.put("player_name", player.getName());
						
						map.put("result", "0");
						
						final String confirm = res.getString("confirm_text");					
						
						Conversation conv = factory.withFirstPrompt(new TestPrompt()).withInitialSessionData(map).withPrefix(new ConversationPrefix(){
							@Override
							public String getPrefix(ConversationContext context)
							{
								return ChatColor.BLUE + "[" + ChatColor.AQUA + "NPC" + ChatColor.BLUE + "] " + ChatColor.WHITE;
							}
						}).withEscapeSequence("exit").buildConversation(Bukkit.getPlayer(player.getName()));
						
						conv.addConversationAbandonedListener(new ConversationAbandonedListener() {
							 
					        @Override
							public void conversationAbandoned(ConversationAbandonedEvent arg0) {
					        	String tmp = null;
					        	IsConvo.put(player.getName(),0);
								
								if(arg0.getContext().getSessionData("result").toString().equalsIgnoreCase("1")) {
									tmp = HollowRPG.textColor(confirm);	
								} else if(arg0.getContext().getSessionData("result").toString().equalsIgnoreCase("12")) {
									World world = player.getWorld();
									int x = (Integer) arg0.getContext().getSessionData("x");
									int y = (Integer) arg0.getContext().getSessionData("y");
									int z = (Integer) arg0.getContext().getSessionData("z");
					                Location location = new Location(world,x,y,z);
									player.teleport(location);
									
									tmp = ChatColor.GREEN + "Welcome to " + arg0.getContext().getSessionData("dest_name").toString() + "\n\n" + ChatColor.AQUA + arg0.getContext().getSessionData("destination").toString() + "\n\n";	
									
								} else {
									tmp = "Goodbye.";
								}
								event.getNPC().getNavigator().getDefaultParameters().baseSpeed((float) 0.8);
								event.getNPC().getNavigator().setTarget(null);
								player.sendMessage(ChatColor.BLUE + "[" + ChatColor.AQUA + "NPC Chat" + ChatColor.BLUE + "] " + ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + event.getNPC().getFullName() + ChatColor.DARK_GREEN + "] " + ChatColor.WHITE + tmp);
								player.setWalkSpeed((float) 0.2);
								
							}

							
					    });
						res.close();
						statement.close();
						c.close();
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
		npc.getNavigator().getDefaultParameters().baseSpeed((float) 0.8);
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