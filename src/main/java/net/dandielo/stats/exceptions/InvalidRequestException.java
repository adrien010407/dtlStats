package net.dandielo.stats.exceptions;

public class InvalidRequestException extends Exception {

	private String request;
	
	public InvalidRequestException(String request)
	{
		this.request = request;
	}

	public String getRequest()
	{
		return request;
	}
	
	private static final long serialVersionUID = 3834429269769998943L;

}
