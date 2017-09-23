package org.oreon.core.gl.shaders.basic;

import org.oreon.core.model.Material;
import org.oreon.core.scene.GameObject;
import org.oreon.core.shaders.Shader;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.ResourceLoader;

public class BasicRGBAShader extends Shader{

	private static BasicRGBAShader instance = null;
	
	public static BasicRGBAShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new BasicRGBAShader();
		}
		return instance;
	}
		
	protected BasicRGBAShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/rgba/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/rgba/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("color");
		addUniform("clipplane");
	}
		
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getWorldTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("clipplane", CoreSystem.getInstance().getRenderingEngine().getClipplane());
		setUniform("color", ((Material) object.getComponent("Material")).getColor());
	}
}
