package org.oreon.core.gl.parameter;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class CullFaceDisable implements RenderParameter{
	
	public void enable(){
		glDisable(GL_CULL_FACE);
	}
	
	public void disable(){
		glEnable(GL_CULL_FACE);
	}		
}