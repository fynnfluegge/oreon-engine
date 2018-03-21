package org.oreon.modules.gl.terrain;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GLTerrainContext {

	private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("terrain-context.xml");
	}
	
	public static TerrainConfiguration getConfiguration(){
		
		return context.getBean(TerrainConfiguration.class);
	}
}
