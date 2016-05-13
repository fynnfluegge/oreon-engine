package engine.shaders.basic;

import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
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

		addVertexShader(ResourceLoader.loadShader("basic/grid/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("basic/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("basic/grid/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("clipplane");
	}
		
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("modelViewProjectionMatrix", modelViewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("clipplane", RenderingEngine.getClipplane());
	}
}
