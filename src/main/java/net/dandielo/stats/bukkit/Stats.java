package net.dandielo.stats.bukkit;

import net.dandielo.stats.bukkit.stats.BukkitStats;
import net.dandielo.stats.bukkit.stats.PlayerStats;
import net.dandielo.stats.bukkit.stats.WorldStats;
import net.dandielo.stats.core.Manager;
import net.dandielo.stats.core.Server;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Stats extends JavaPlugin {
	//console prefix
	public static final String PREFIX = "[dtlStats]" + ChatColor.WHITE; 
	
	//bukkit resources
	private static ConsoleCommandSender console;

	//The server instance
	private Server server;
	private static Stats instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		console = Bukkit.getConsoleSender();
		
		this.saveDefaultConfig();
		
		//get the port that we will listen to :)
		Server.port = getConfig().getInt("port", Server.port);
		Server.init();
		
		server = Server.instance;
		
		//bukkit listener and updater
		Manager.registerListener("dtlStats", BukkitStats.class);
		Manager.registerUpdater("dtlStats", BukkitStats.class);
		
		//player listener
		Manager.registerListener("dtlStats", PlayerStats.class);
		
		//world listener
		Manager.registerListener("dtlStats", WorldStats.class);
		
		info("Enabled dtlStats beta");
	}
	
	public static Stats getInstance()
	{
		return instance;
	}
	
	@Override
	public void onDisable()
	{
		server.disconnect();
		try
		{
			server.join(500);
		} catch ( Exception e ) { }
	}	

	//static logger warning
	public static void info(String message)
	{
		console.sendMessage(PREFIX + "[INFO] " + message);
	}
	
	//static logger warning
	public static void warning(String message)
	{
		console.sendMessage(PREFIX + ChatColor.GOLD + "[WARNING] " + ChatColor.RESET + message);
	}
	
	//static logger severe
	public static void severe(String message)
	{
		console.sendMessage(PREFIX + ChatColor.RED + "[SEVERE] " + ChatColor.RESET + message);
	}
}
