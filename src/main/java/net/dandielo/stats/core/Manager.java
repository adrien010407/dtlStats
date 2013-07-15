package net.dandielo.stats.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.dandielo.stats.api.Listener;
import net.dandielo.stats.api.Stat;
import net.dandielo.stats.api.Updater;

public class Manager {
	public static Manager instance = new Manager();
	
	/**
	 * Listens to data request from incoming connections
	 */
	Map<String, Listener> listeners = new HashMap<String, Listener>();
	
	/**
	 * Handles incoming data updates
	 */
	Map<String, Updater> updaters = new HashMap<String, Updater>();
	
	/**
	 * A new manager instance
	 */
	private Manager()
	{
	}
	
	public int getListenerCount()
	{
		return listeners.size();
	}
	
	/**
	 * Registers a new listener for the given plugin
	 * @param plugin
	 * The plugin
	 * @param listener
	 * that will be registered
	 */
	public static void registerListener(String plugin, Listener listener)
	{
		instance.listeners.put(plugin, listener);
	}
	
	/**
	 * Registers a new listener for the given plugin
	 * @param plugin
	 * The plugin
	 * @param listener
	 * that will be registered
	 */
	public static void registerUpdater(String plugin, Updater updater)
	{
		instance.updaters.put(plugin, updater);
	}
	
	/**
	 * Searches for the requested plugin updater, if found calls an update event. 
	 * @param plugin
	 * The requested plugin updater
	 * @param stat
	 * The requested stat to update
	 * @param value
	 * The new value that will be set
	 */
	public static void update(String plugin, String stat, Object value)
	{
		try
		{
			instance.__update(plugin, stat, value);
		}
		catch( Exception e ) { }
	}
	
	public void __update(String plugin, String stat, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if ( updaters.get(plugin) == null ) return;
		
		for ( Method method : updaters.get(plugin).getClass().getMethods() )
		{
			if ( method.isAnnotationPresent(Stat.class) )
			if ( method.getAnnotation(Stat.class).name().equals(stat) )
			{
				method.invoke(updaters.get(plugin), value);
			}
		}
	}

	/**
	 * Searches for the requested plugin listener, if found calls an event to get the stat value. 
	 * @param plugin
	 * The plugin that holds out stat
	 * @param stat
	 * The stat which value will be gathered
	 */
	public static Object get(String plugin, String stat)
	{
		try
		{
			return instance.__get(plugin, stat);
		}
		catch( Exception e ) { }
		return null;
	}
	
	public Object __get(String plugin, String stat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if ( listeners.get(plugin) == null ) return null;
		
		for ( Method method : listeners.get(plugin).getClass().getMethods() )
		{
			if ( method.isAnnotationPresent(Stat.class) )
			if ( method.getAnnotation(Stat.class).name().equals(stat) )
			{
				return method.invoke(listeners.get(plugin));
			}
		}
		return null;
	}
}
