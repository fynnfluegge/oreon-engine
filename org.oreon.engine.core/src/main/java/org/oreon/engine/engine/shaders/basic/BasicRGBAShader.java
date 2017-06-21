package org.oreon.engine.engine.shaders.basic;

import org.oreon.engine.engine.core.RenderingEngine;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

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
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniform("color", ((Material) object.getComponent("Material")).getColor());
	}
}
