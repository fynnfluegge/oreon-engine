package engine.shaderprograms.basic;

import engine.core.ResourceLoader;
import engine.gameObject.components.Material;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.shaderprograms.Shader;

public class RGBA extends Shader{

	private static RGBA instance = null;
	
	public static RGBA getInstance() 
	{
		if(instance == null) 
		{
			instance = new RGBA();
		}
		return instance;
	}
		
	protected RGBA()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("basic/rgba/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("basic/rgba/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("color");
		addUniform("clipplane");
	}
		
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("modelViewProjectionMatrix", modelViewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("clipplane", RenderingEngine.getClipplane());
	}
		
	public void sendUniforms(Material material)
	{
		setUniform("color", material.getColor());
	}
}
