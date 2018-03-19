package org.oreon.core.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.oreon.core.math.Quaternion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonConfig {
	
	protected static CommonConfig instance = null;
	
	public static CommonConfig getInstance(){
		
		if(instance == null){
			instance = new CommonConfig();
		}
		return instance;
	}
	
	// render configurations
	private boolean wireframe;
	private boolean underwater;
	private boolean reflection;
	private boolean refraction;
	
	// post processing filter
	private boolean bloomEnabled;
	private boolean dephtOfFieldEnabled;
	
	// render settings
	private float sightRange;
	private Quaternion clipplane;
	
	private int multisamples;
	
	protected CommonConfig(){
		
		Properties properties = new Properties();
		try {
			InputStream stream = CommonConfig.class.getClassLoader().getResourceAsStream("common-config.properties");
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		multisamples = Integer.valueOf(properties.getProperty("multisamples"));
	}
}
