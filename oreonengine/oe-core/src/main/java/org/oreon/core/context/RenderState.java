package org.oreon.core.context;

import org.oreon.core.math.Quaternion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RenderState {
	
	// render parameters
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
