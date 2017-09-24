package org.oreon.core.gl.shaders.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.ResourceLoader;

public class BasicTexturedShader extends GLShader{

	private static BasicTexturedShader instance = null;
	
	public static BasicTexturedShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BasicTexturedShader();
	    }
	      return instance;
	}
	
	protected BasicTexturedShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/texture/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/texture/Fragment.glsl"));
		compileShader();
		
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("texture");
		addUniform("clipplane");
	}
	
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());	
		
		glActiveTexture(GL_TEXTURE0 );
		((Material) object.getComponents().get("Material")).getDiffusemap().bind();
		setUniformi("texture", 0);
	}
}
