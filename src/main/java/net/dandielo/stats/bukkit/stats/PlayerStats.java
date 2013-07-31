package net.dandielo.stats.bukkit.stats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;

import net.dandielo.stats.api.Listener;
import net.dandielo.stats.api.Stat;
import net.dandielo.stats.core.response.JSonResponse;

@Stat(name = "players")
public class PlayerStats implements Listener {
	
	/**
	 * Returns a list of players on the server
	 * @throws JSONException 
	 */
	@Stat(name = "list")
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
		response.key("count").value(Bukkit.getOnlinePlayers().length);		
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
	 */
	/*@Stat(name = "{name}/info/{attr}")
	public String attr(String name, String attr)
	{
		Player player = Bukkit.getOfflinePlayer(name).getPlayer();
		if ( player == null ) return "Player does not exists";
		
		PlayerAttr eAttr = PlayerAttr.valueOf(attr.toUpperCase());
		if ( eAttr == null )
			return "Wrong attribute";
		
		switch(eAttr)
		{
	//	case HEALTH: return String.format("%.2f", player.getHealth());
	//	case MAX_HEALTH: return String.format("%.2f", player.getMaxHealth());
	//	case ABSORB: 
		case EXP: return String.format("%.2f", player.getExp());
		case TOTAL_EXP: return String.valueOf(player.getTotalExperience());
		case EXP_TO_LVL: return String.valueOf(player.getExpToLevel());
		case EXHAUST: return String.format("%.2f", player.getExhaustion());
		case FOOD: return String.valueOf(player.getFoodLevel());
		case AIR: return String.valueOf(player.getRemainingAir());
		case MAX_AIR: return String.valueOf(player.getMaximumAir());
		case LVL: return String.valueOf(player.getLevel());
		case LIVED: return String.valueOf(player.getTicksLived());
		case ONLINE: return String.valueOf(player.isOnline());
		case DEAD: return String.valueOf(player.isDead());
		case FLYING: return String.valueOf(player.isFlying());
		case OP: return String.valueOf(player.isOp());
		case SLEEPING: return String.valueOf(player.isSleeping());
		case SNEAKING: return String.valueOf(player.isSneaking());
		case SPRINTING: return String.valueOf(player.isSprinting());
		case WHITELISTED: return String.valueOf(player.isWhitelisted());
		case BANNED: return String.valueOf(player.isBanned());
		case GAMEMODE: return player.getGameMode().name().toLowerCase();
		default: return "unsupported";
		}
	}*/
	
	/**
	 * Returns information of a specific player
	 * @param name
	 * of the player that will be checked for stats
	 * @param attr
	 * that will be returned
	 * @throws JSONException 
	 */
	@Stat(name = "player/{name}")
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
		
		//food, exhaust, lvl
		response.key("level").value(player.getLevel());
		response.key("food").value(player.getFoodLevel());
		response.key("exhaustion").value(player.getExhaustion());
		
		//air
		response.key("air").value(player.getRemainingAir());
		response.key("airMax").value(player.getMaximumAir());
		
		//health
	//	response.key("health").value(player.getHealth());
	//	response.key("health_max").value(player.getMaxHealth());
		
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
	 * @param attr
	 * of the players location 
	 */
	/*@Stat(name = "{name}/loc/{attr}")
	public String loc(String name, String attr)
	{
		Player player = Bukkit.getOfflinePlayer(name).getPlayer();
		if ( player == null ) return "Player does not exists";
		
		PlayerAttr eAttr = PlayerAttr.valueOf(attr.toUpperCase());
		if ( eAttr == null )
			return "Wrong attribute";
		
		switch(eAttr)
		{
		case X: return String.format("%.3f", player.getLocation().getX());
		case Y: return String.format("%.3f", player.getLocation().getY());
		case Z: return String.format("%.3f", player.getLocation().getZ());
		case WORLD: return player.getWorld().getName();
		case WORLD_TYPE: return player.getWorld().getWorldType().getName();
		default: return "unsupported";
		}
	}*/
	
	/**
	 * Returns information of a specific player
	 * @param name
	 * of the player that will be checked for stats
	 * @param slot
	 * that will be returned with item information
	 * @throws JSONException 
	 */
	@Stat(name = "player/{name}/inv")
	public Object inv(String name) throws JSONException
	{
		Player player = Bukkit.getPlayer(name);
		if ( player == null ) return "Player does not exists";
		
		//create a JSonResponse
		JSonResponse response = new JSonResponse();
		response.object();
		
		//item in hand
		response.key("itemInHand").value(player.getItemInHand().serialize());
		
		//armor subclass
		response.key("armor").object();
		response.set("helmet", player.getInventory().getHelmet().serialize());
		response.set("chest", player.getInventory().getChestplate().serialize());
		response.set("leggings", player.getInventory().getLeggings().serialize());
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
	
	public static void main(String[] a) throws JSONException
	{
		JSonResponse r = new JSonResponse();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", 3);
		map.put("no_test", "but still");
		r.object().key("asd").object().key("Avidi").value(map).endobject().endobject();
		
		System.out.print(r.stringResponse());
	}
	
	/**
	 * Returns information of a specific player
	 * @param name
	 * of the player that will be checked for stats
	 * @param slot
	 * that will be returned with item information
	 */
	
	enum PlayerAttr
	{
		//numbers
		HEALTH, ABSORB, DEFENSE, EXHAUST, EXP, EXP_TO_LVL, FOOD, MAX_HEALTH, MAX_AIR, AIR, LVL,
		TOTAL_EXP, LIVED,
		//positon 
		X, Y, Z, WORLD, WORLD_TYPE, WORLD_NAME,
		//items
		INV, IN_HAND, ENDER, ARMOR, 
		//other 
		GAMEMODE, ONLINE, SNEAKING, WHITELISTED, BANNED, DEAD, SLEEPING, OP, FLYING, SPRINTING;
	}
}
