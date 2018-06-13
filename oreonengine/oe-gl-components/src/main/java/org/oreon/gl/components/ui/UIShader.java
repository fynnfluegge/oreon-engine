package org.oreon.gl.components.ui;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.ResourceLoader;

public class UIShader extends GLShaderProgram{

	private static UIShader instance = null;
	
	public static UIShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new UIShader();
	    }
	    return instance;
	}
	
	protected UIShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/gui/gui_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/gui/gui_FS.glsl"));
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
