package org.oreon.core.gl.shaders.basic;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scene.GameObject;
import org.oreon.core.util.ResourceLoader;

public class BasicGridShader extends GLShader{

	private static BasicGridShader instance = null;
	
	public static BasicGridShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new BasicGridShader();
		}
		return instance;
	}
		
	protected BasicGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/grid/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/basic/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/grid/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("color");
	}
		
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("color", new Vec3f(0.1f,0.9f,0.1f));
	}
}
