package net.dandielo.stats.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.dandielo.stats.api.Stat.RequestType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Stat {
	public String name();
	public RequestType requestType() default AUTO;
	
	public static enum RequestType
	{
		GET, UPDATE, AUTO;

		public boolean auto()
		{
			return this.equals(AUTO);
		}
		
		public boolean get()
		{
			return this.equals(GET);
		}
		
		public boolean update()
		{
			return this.equals(UPDATE);
		}
	}
}
