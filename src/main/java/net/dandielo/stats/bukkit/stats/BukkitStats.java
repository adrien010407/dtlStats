package net.dandielo.stats.bukkit.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import net.dandielo.stats.api.Listener;
import net.dandielo.stats.api.Stat;
import net.dandielo.stats.api.Stat.RequestType;
import net.dandielo.stats.api.Updater;
import net.dandielo.stats.core.Response;
import net.dandielo.stats.core.response.JSonResponse;

@Stat(name = "bukkit")
public class BukkitStats implements Listener, Updater, org.bukkit.event.Listener {
	
	private static class CommandCondition
	{
		String command;
		
		Condition condition;
		String conditionArg; 
		
		public CommandCondition(String command, String arg)
		{
			condition = Condition.ON_LOGIN;
			this.command = command;
			this.conditionArg = arg;
		}
		
		static enum Condition {
			ON_LOGIN
		}
	}

	//all awaiting commands
	private Set<CommandCondition> commands = new HashSet<CommandCondition>();
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event)
	{
		Iterator<CommandCondition> it = commands.iterator();
		boolean done = false;
		while ( it.hasNext() && !done )
		{
			CommandCondition command = it.next();
			if ( command.condition.equals(CommandCondition.Condition.ON_LOGIN) )
				if ( command.conditionArg.equalsIgnoreCase(event.getPlayer().getName()) )
				{
					//dispatch the command
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.command);
					
					//remove it
					it.remove();
					done = true;
				}
		}
	}
	
	@Stat(name = "command/{command}/{condition}", requestType = RequestType.UPDATE)
	public void conditionedCommand(String command, String condition, String arg)
	{
		//do the requested command
		commands.add(new CommandCondition(command, arg));
	}

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
			response.set(plugin.getName(), plugin.getDescription().getVersion());
		response.endobject();
		
		//server info
		response.key("server").object();
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
