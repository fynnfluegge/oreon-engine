package org.oreon.engine.engine.shaders.gui;

import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

public class GuiShader extends Shader{

	private static GuiShader instance = null;
	
	public static GuiShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GuiShader();
	    }
	    return instance;
	}
	
	protected GuiShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/gui/Vertex.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/gui/Fragment.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("texture");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(int texture)
	{
		setUniformi("texture", texture);
	}
}
