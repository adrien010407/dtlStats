package net.dandielo.stats.core.response;

import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONWriter;

import net.dandielo.stats.core.Response;

/**
 * Creates a JSon response for the requested data. 
 * @author dandielo
 *
 */
public class JSonResponse extends Response {
	
	//String writer and JSon builder
	private StringWriter writer;
	private JSONWriter json;
	
	/**
	 * Creates a new JSon response object, allows to create JSon data and send it through the socket
	 */
	public JSonResponse()
	{
		//init the writer and builder
		writer = new StringWriter();
		json = new JSONWriter(writer);
	}

	/**
	 * Changes the supplied object into a JSon Object with the specified key.
	 * @param key
	 * that will be used to store the object
	 * @param obj
	 * the prepared object which values will be stored
	 * @return
	 * JSonResponse object
	 * @throws JSONException
	 */
	public JSonResponse addObject(String key, Object obj)
	{
		//search for fields with the JSonField annotation
		for ( Field field : obj.getClass().getDeclaredFields() )
		{
			//get the JSonTarget
			JSonTarget jf = field.getAnnotation(JSonTarget.class);
			if ( jf != null )
			{
				//set the field accessible
				if ( !field.isAccessible() )
					field.setAccessible(true);
				
				//try to create an key and value for the field
				try
				{
					//get the field value
					Object value = field.get(obj);
					
					//set key and object
					json.key(jf.name().isEmpty() ? field.getName() : jf.name()).value(value);
				} catch(Exception e) { }

				//set unaccessible
				field.setAccessible(false);
			}
		}
		return this;
	}
	
	public JSonResponse set(String key, Object value) throws JSONException
	{
		//set key and value
		key(key).value(value);
		return this;
	}
	
	public JSonResponse object() throws JSONException
	{
		json.object();
		return this;
	}
	
	public JSonResponse endobject() throws JSONException
	{
		json.endObject();
		return this;
	}
	
	public JSonResponse array() throws JSONException
	{
		json.array();
		return this;
	}
	
	public JSonResponse endarray() throws JSONException
	{
		json.endArray();
		return this;
	}
	
	public JSonResponse key(String key) throws JSONException
	{
		json.key(key);
		return this;
	}
	
	public JSonResponse value(Object value) throws JSONException
	{
		json.value(value);
		return this;
	}

	@Override
	public String stringResponse()
	{
		return writer.toString();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public static @interface JSonTarget
	{
		public String name() default "";
	}
}
