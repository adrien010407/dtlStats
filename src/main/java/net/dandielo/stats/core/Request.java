package net.dandielo.stats.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request implements Runnable {

	private Socket socket;
	private boolean valid = true;

	public Request(Socket incoming)
	{
		this.socket = incoming;
	}

	@Override
	public void run()
	{
		try
		{
			// Get input from the client
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			RequestType req;
			String line;
			//handle the incoming connection
			while(valid)
			{
				line = null;
				req = null;
				
				while((line = in.readLine()) != null && 
					  (req = RequestType.valueOf(line)).invalid() );
				
				//if disconnect then disconnect, lol
				if ( req == null || req.disconnect() ) valid = false;

				//if its a read request
				if ( valid && req.update() )
				{
					RequestInfo info = new RequestInfo(in.readLine());
					
					//update the stat
					Manager.update(info.get("plugin"), info.get("stat"), info.get("value"));
				}
				
				//if it's a update request
				if ( valid && req.get() )
				{
					RequestInfo info = new RequestInfo(in.readLine());
					
					//get the stat value
					Object result = Manager.get(info.get("plugin"), info.get("stat"));
					
					//send it back
					out.println(result);
				}
			}

			//close the socket
			socket.close();
		}
		catch( IOException e ) { }
	}
	
	static class RequestInfo
	{
		//the pattern used to split the request string
		private static Pattern pattern = Pattern.compile("(([^:]+):{0,1})");
		private static String[] field = { "plugin", "stat", "value" };

		private Map<String, String> data = new HashMap<String, String>();
		
		public RequestInfo(String data)
		{
			Matcher matcher = pattern.matcher(data);
			
			int i = 0;
			while(matcher.find())
				this.data.put(field[i++], matcher.group(2));
		}
		
		public String get(String field)
		{
			return data.get(field);
		}
	}
	
	enum RequestType
	{
		UPDATE, GET, DISCONNECT, INVALID;
		
		public static RequestType byId(int id)
		{
			switch(id)
			{
			case 0x1: return DISCONNECT;
			case 0x2: return UPDATE;
			case 0x4: return GET;
			default: return INVALID;
			}
		}

		public boolean update()
		{
			return this.equals(UPDATE);
		}

		public boolean get()
		{
			return this.equals(GET);
		}

		public boolean invalid()
		{
			return this.equals(INVALID);
		}

		public boolean disconnect()
		{
			return this.equals(DISCONNECT);
		}
	}
}
