package net.dandielo.stats.bukkit.stats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.json.JSONException;

import net.dandielo.api.stats.Listener;
import net.dandielo.api.stats.Stat;
import net.dandielo.stats.core.response.JSonResponse;

@Stat(name = "worlds")
public class WorldStats implements Listener {
	
	/**
	 * Returns a list of players on the server
	 * @throws JSONException 
	 */
	@Stat(name = "list")
	public Object list() throws JSONException
	{
		JSonResponse response = new JSonResponse();
		
		//offline players
		response.object();
		
		for ( World world : Bukkit.getWorlds() )
			response.key(world.getName()).value(world.getWorldType().name());
		
		//send the response back
		response.endobject();
		return response;
	}
	
	/**
	 * Returns information of a specific world
	 * @param name
	 * of the world that will be checked for stats
	 * @param attr
	 * that will be returned
	 * @throws JSONException 
	 */
	@Stat(name = "world/{name}")
	public Object worldInfo(String name) throws JSONException
	{
		//check if the player exists
		World world  = Bukkit.getWorld(name);
		if ( world == null ) return "World does not exists";
		
		//create aJSonResponse object
		JSonResponse response = new JSonResponse();
		response.object();
		
		//world stats
		response.key("entities").object(); 
		response.set("count", world.getEntities().size());
		response.set("living", world.getLivingEntities().size());
		
		Map<EntityType, Number> entities = new HashMap<EntityType, Number>();
		for ( Entity entity : world.getEntities() )
			if ( entities.containsKey(entity.getType()) )
				entities.put(entity.getType(), entities.get(entity.getType()).intValue() + 1);
			else
				entities.put(entity.getType(), 1);
		
		for ( Map.Entry<EntityType, Number> entry : entities.entrySet() )
			response.set(entry.getKey().name().toLowerCase(), entry.getValue().intValue());
			
		response.endobject();
		
		response.key("spawnLimit").object();
		response.set("monster", world.getMonsterSpawnLimit());
		response.set("animal", world.getAnimalSpawnLimit());
		response.set("wateranimal", world.getWaterAnimalSpawnLimit());
		response.set("ambient", world.getAmbientSpawnLimit());
		response.endobject();
		
		response.set("allowMonsters", world.getAllowMonsters());
		response.set("allowAnimals", world.getAllowAnimals());
		response.set("difficulty", world.getDifficulty());
		response.set("pvp", world.getPVP());
		
		response.set("seed", world.getSeed());
		response.set("environment", world.getEnvironment().name());
		response.set("maxHeight", world.getMaxHeight());
		response.set("seaLevel", world.getSeaLevel());
		
		//end the object
		response.endobject();
		return response;
	}
}
