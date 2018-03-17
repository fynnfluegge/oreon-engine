package org.oreon.core.system;

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
	
	protected CommonConfig(){
		
		// TODO load properties
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
}
