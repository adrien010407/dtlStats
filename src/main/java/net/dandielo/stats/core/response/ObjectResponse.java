package net.dandielo.stats.core.response;

import net.dandielo.stats.core.Response;

public class ObjectResponse extends Response {
	private Object response;
	
	public ObjectResponse(Object o)
	{
		response = o;
	}
	
	@Override
	public String stringResponse()
	{
		return String.valueOf(response);
	}
	
}
