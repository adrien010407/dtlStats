package net.dandielo.stats.bukkit.stats;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;

import net.dandielo.api.stats.Listener;
import net.dandielo.api.stats.Stat;
import net.dandielo.api.stats.Stat.RequestType;
import net.dandielo.api.stats.Updater;
import net.dandielo.stats.core.response.JSonResponse;

@Stat(name = "players")
public class PlayerStats implements Listener, Updater {
	
	/**
	 * Returns a list of players on the server
	 * @throws JSONException 
	 */
	@Stat(name = "list", requestType = RequestType.GET)
	public Object list() throws JSONException
	{
		JSonResponse response = new JSonResponse();
		
		//offline players
		response.object().key("all").object();
		//count all players
		response.key("count").value(Bukkit.getOfflinePlayers().length);
		//add each player
		for ( OfflinePlayer player : Bukkit.getOfflinePlayers() )
			response.key(player.getName()).value(player.getLastPlayed());
		response.endobject();
		
		//online players
		response.key("online").object();
		//count all online players
		response.key("count").value(Bukkit.getOnlinePlayers().size());		
		//add each player
		for ( Player player : Bukkit.getOnlinePlayers() )
			response.key(player.getName()).value(player.getTicksLived());
		response.endobject();
		
		//send the response back
		response.endobject();
		return response;
	}
	
	/**
	 * Returns information of a specific player
	 * @param name
	 * of the player that will be checked for stats
	 * @param attr
	 * that will be returned
	 * @throws JSONException 
	 */
	@Stat(name = "player/{name}", requestType = RequestType.GET)
	public Object playerInfo(String name) throws JSONException
	{
		//check if the player exists
		Player player = Bukkit.getPlayer(name);
		if ( player == null ) return "Player does not exists";
		
		//create aJSonResponse object
		JSonResponse response = new JSonResponse();
		response.object();
		
		//experience
		response.key("exp").value(player.getExp());
		response.key("expTotal").value(player.getTotalExperience());
		response.key("expToLvl").value(player.getExpToLevel());
		response.key("test").value("test value");
		
		//food, exhaust, lvl
		response.key("level").value(player.getLevel());
		response.key("food").value(player.getFoodLevel());
		response.key("exhaustion").value(player.getExhaustion());
		
		//air
		response.key("air").value(player.getRemainingAir());
		response.key("airMax").value(player.getMaximumAir());
		
		//health
		response.key("health").value(player.getHealth());
		response.key("health_max").value(player.getMaxHealth());
		
		//flags
		response.key("gamemode").value(player.getGameMode().name().toLowerCase());
		response.key("lived").value(player.getTicksLived());
		response.key("isOnline").value(player.isOnline());
		response.key("isDead").value(player.isDead());
		response.key("isFlying").value(player.isFlying());
		response.key("isOp").value(player.isOp());
		response.key("isSleeping").value(player.isSleeping());
		response.key("isSneaking").value(player.isSneaking());
		response.key("isSprinting").value(player.isSprinting());
		response.key("isWhitelisted").value(player.isWhitelisted());
		response.key("isBanned").value(player.isBanned());
		
		//location
		response.key("x").value(player.getLocation().getBlockX());
		response.key("y").value(player.getLocation().getBlockY());
		response.key("z").value(player.getLocation().getBlockZ());
		response.key("world").value(player.getWorld().getName());
		
		//end the object
		response.endobject();
		return response;
	}
	
	/**
	 * Returns information of a specific player
	 * @param name
	 * of the player that will be checked for stats
	 * @param slot
	 * that will be returned with item information
	 * @throws JSONException 
	 */
	@Stat(name = "player/{name}/inv", requestType = RequestType.GET)
	public Object inv(String name) throws JSONException
	{
		Player player = Bukkit.getPlayer(name);
		if ( player == null ) return "Player does not exists";
		
		//create a JSonResponse
		JSonResponse response = new JSonResponse();
		response.object();
		
		//item in hand
		if ( player.getItemInHand() != null )
			response.key("itemInHand").value(player.getItemInHand().serialize());
		
		//armor subclass
		response.key("armor").object();
		if ( player.getInventory().getHelmet() != null )
			response.set("helmet", player.getInventory().getHelmet().serialize());
		if ( player.getInventory().getChestplate() != null )
			response.set("chest", player.getInventory().getChestplate().serialize());
		if ( player.getInventory().getLeggings() != null )
			response.set("leggings", player.getInventory().getLeggings().serialize());
		if ( player.getInventory().getBoots() != null )
			response.set("boots", player.getInventory().getBoots().serialize());
		response.endobject();
		
		//inv subclass
		response.key("inventory").object();
		int slot = 0;
		for ( ItemStack item : player.getInventory() )
		{
			if ( item != null )
			    response.set(String.valueOf(slot), item.serialize());
			++slot;
		}
		response.endobject();
		
		//ender chest subclass
		response.key("enderChest").object();
		slot = 0;
		for ( ItemStack item : player.getEnderChest() )
		{
			if ( item != null )
			    response.set(String.valueOf(slot), item.serialize());
			++slot;
		}
		response.endobject();
	
		//send the response
		response.endobject();
		return response;
	}
	
	@Stat(name = "player/{name}/inv/{slot}", requestType = RequestType.UPDATE)
	public Object itemMove(String name, String slot1, String slot2) 
	{
		Player player = Bukkit.getPlayer(name);
		if ( player == null ) return "Player does not exists";
		
		Inventory inv = player.getInventory();
		int s1 = Integer.parseInt(slot1);
		int s2 = Integer.parseInt(slot2);
		
		ItemStack is = inv.getItem(s1);
		inv.setItem(s1, inv.getItem(s2));
		inv.setItem(s2, is);
		return null;
	}
	
}
