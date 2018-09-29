package org.oreon.core.context;

import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.Camera;

import lombok.Getter;

public abstract class EngineContext {

	@Getter
	private static Configuration config;
	@Getter
	private static GLFWInput input;
	@Getter
	protected static Camera camera;
	@Getter
	protected static Window window;
	
	public static void init() {
		config = new Configuration();
		input = new GLFWInput();
	}

}
