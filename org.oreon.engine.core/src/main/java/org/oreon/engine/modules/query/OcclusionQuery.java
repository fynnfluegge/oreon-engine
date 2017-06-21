package org.oreon.engine.modules.query;
import static org.lwjgl.opengl.GL15.glBeginQuery;
import static org.lwjgl.opengl.GL15.glDeleteQueries;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_SAMPLES_PASSED;
import static org.lwjgl.opengl.GL15.glGenQueries;
import static org.lwjgl.opengl.GL15.glEndQuery;
import static org.lwjgl.opengl.GL15.glGetQueryObject;
import static org.lwjgl.opengl.GL15.glGetQueryObjectui;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDepthMask;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT;
import static org.lwjgl.opengl.GL15.GL_QUERY_RESULT_AVAILABLE;

import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.utils.BufferUtil;
import org.oreon.engine.modules.lighting.Light;

public class OcclusionQuery {

	private int id;
	private IntBuffer buffer;
	private int occlusionFactor;
	
	public OcclusionQuery(){
		buffer = BufferUtil.createIntBuffer(1);
		id = glGenQueries();
	}
	
	public void doQuery(GameObject object){
		
		glColorMask(false, false, false, false);
		glDepthMask(false);
		
		 // Begin occlusion query
		glBeginQuery(GL_SAMPLES_PASSED, id);
        // Every pixel that passes the depth test now gets added to the result

        object.render();
        glFinish();
        glEndQuery(GL_SAMPLES_PASSED);
    	
        // Now get the number of pixels passed
        int querystate = 0;
        while (querystate == 0){
        	querystate = glGetQueryObjectui(id, GL_QUERY_RESULT_AVAILABLE);
        }
        glFinish();
        glGetQueryObject(id, GL_QUERY_RESULT, buffer);
    	
        occlusionFactor = buffer.get(0);
        
    	glColorMask(true, true, true, true);
		glDepthMask(true);
	}
	
	public void doQuery(Light light){
		
		glColorMask(false, false, false, false);
		glDepthMask(false);
		
		 // Begin occlusion query
		glBeginQuery(GL_SAMPLES_PASSED, id);
        // Every pixel that passes the depth test now gets added to the result

        light.getParent().render();
        glFinish();
        glEndQuery(GL_SAMPLES_PASSED);
        
    	// Now get the number of pixels passed
        int querystate = 0;
        while (querystate == 0){
        	querystate = glGetQueryObjectui(id, GL_QUERY_RESULT_AVAILABLE);
        }
        glFinish();
        glGetQueryObject(id, GL_QUERY_RESULT, buffer);
    	
        occlusionFactor = buffer.get(0);
        
        if (occlusionFactor < 8000)
        	occlusionFactor = 0;
        
    	glColorMask(true, true, true, true);
		glDepthMask(true);
	}
	
	public void delete() {
		glDeleteQueries(id);
	}

	public int getOcclusionFactor() {
		return occlusionFactor;
	}

	public void setOcclusionFactor(int occlusionFactor) {
		this.occlusionFactor = occlusionFactor;
	}
}
