package org.oreon.engine.apps.samples.fractalworlds;

import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;

public class PssmGUIShader extends Shader{

	private static PssmGUIShader instance = null;

	
	public static PssmGUIShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new PssmGUIShader();
	    }
	    return instance;
	}
	
	protected PssmGUIShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("samples/FractalWorlds/GUI/PssmGUI_VS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("samples/FractalWorlds/GUI/PssmGUI_FS.glsl"));
		compileShader();
		
		addUniform("orthographicMatrix");
		addUniform("tex");
		addUniform("split");
	}
	
	public void updateUniforms(Matrix4f orthographicMatrix)
	{
		setUniform("orthographicMatrix", orthographicMatrix);
	}
	
	public void updateUniforms(int texture, float split)
	{
		setUniformi("tex", texture);
		setUniformf("split", split);
	}
}
