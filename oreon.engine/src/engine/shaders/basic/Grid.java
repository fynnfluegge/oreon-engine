package engine.shaders.basic;

import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shaders.Shader;

public class Grid extends Shader{

	private static Grid instance = null;
	
	public static Grid getInstance() 
	{
		if(instance == null) 
		{
			instance = new Grid();
		}
		return instance;
	}
		
	protected Grid()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/grid/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/basic/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/grid/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("clipplane");
		addUniform("color");
	}
		
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("clipplane", RenderingEngine.getClipplane());
		setUniform("color", ((Material) object.getComponent("Material")).getColor());
	}
}
