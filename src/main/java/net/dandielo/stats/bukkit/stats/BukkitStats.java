package net.dandielo.stats.bukkit.stats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import net.dandielo.api.stats.Listener;
import net.dandielo.api.stats.Stat;
import net.dandielo.api.stats.Updater;
import net.dandielo.api.stats.Stat.RequestType;
import net.dandielo.stats.core.Response;
import net.dandielo.stats.core.response.JSonResponse;

@Stat(name = "bukkit")
public class BukkitStats implements Listener, Updater, org.bukkit.event.Listener {

	@Stat(name = "command", requestType = RequestType.UPDATE)
	public void command(String command)
	{
		//do the requested command
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	@Stat(name = "broadcast", requestType = RequestType.UPDATE)
	public void broadcast(String message)
	{
		Bukkit.broadcastMessage(message);
	}

	@Stat(name = "info", requestType = RequestType.GET)
	public Response info() throws JSONException
	{
		JSonResponse response = new JSonResponse();
		response.object();
		
		//get all plugins
		response.key("plugins").object();
		for ( Plugin plugin : Bukkit.getPluginManager().getPlugins() )
		{
			response.set(plugin.getName(), plugin.getDescription().getVersion());
		}
		response.endobject();
		
		//server info
		response.key("info").object();
		response.set("ip", Bukkit.getIp());
		response.set("port", Bukkit.getPort());
		response.set("motd", Bukkit.getMotd());
		response.set("name", Bukkit.getName());
		response.set("version", Bukkit.getBukkitVersion());
		response.set("maxPlayers", Bukkit.getMaxPlayers());
		response.set("onlineMode", Bukkit.getOnlineMode());
		response.set("defaultGamemode", Bukkit.getDefaultGameMode());
		response.set("isHardcore", Bukkit.isHardcore());
		response.endobject();
		
		response.endobject();
		return response;
	}
	
	
}
