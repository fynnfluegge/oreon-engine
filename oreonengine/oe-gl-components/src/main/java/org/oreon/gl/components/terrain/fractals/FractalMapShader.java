package org.oreon.gl.components.terrain.fractals;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.util.ResourceLoader;

public class FractalMapShader extends GLShaderProgram{
	
	private static FractalMapShader instance = null;

	public static FractalMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new FractalMapShader();
	    }
	      return instance;
	}
	
	protected FractalMapShader() {
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/terrain/FractalMap.comp"));
		compileShader();
		
		addUniform("N");
		addUniform("edgeElevation");
		
		for (int i=0; i<8; i++){
			addUniform("fractals[" + i + "].dy");
			addUniform("fractals[" + i + "].dx");
			addUniform("fractals[" + i + "].dz");
			addUniform("fractals[" + i + "].normalmap");
			addUniform("fractals[" + i + "].scaling");
			addUniform("fractals[" + i + "].verticalStrength");
			addUniform("fractals[" + i + "].horizontalStrength");
			addUniform("fractals[" + i + "].choppy");
		}
	}
	
	public void updateUniforms(List<FractalMap> fractals, int N, boolean edgeElevation){
		
		setUniformi("N", N);
		setUniformi("edgeElevation", edgeElevation ? 1 : 0);
		
		for (int i=0; i<8; i++)
		{
			glActiveTexture(GL_TEXTURE0 + i * 4);
			fractals.get(i).getHeightmap().bind();
			glActiveTexture(GL_TEXTURE1 + i * 4);
			fractals.get(i).getDxDisplacement().bind();
			glActiveTexture(GL_TEXTURE2 + i * 4);
			fractals.get(i).getDzDisplacement().bind();
			
			glActiveTexture(GL_TEXTURE3 + i * 4);
			fractals.get(i).getNormalmap().bind();
			
			setUniformi("fractals[" + i +"].dy", 0 + i * 4);	
			setUniformi("fractals[" + i +"].dx", 1 + i * 4);	
			setUniformi("fractals[" + i +"].dz", 2 + i * 4);	
			setUniformi("fractals[" + i +"].normalmap", 3 + i * 4);	
			setUniformi("fractals[" + i +"].scaling", fractals.get(i).getScaling());
			setUniformf("fractals[" + i +"].verticalStrength", fractals.get(i).getHeightStrength());
			setUniformf("fractals[" + i +"].horizontalStrength", fractals.get(i).getHorizontalStrength());
			setUniformi("fractals[" + i +"].choppy", fractals.get(i).isChoppy() ? 1 : 0);
		}
	}

}
