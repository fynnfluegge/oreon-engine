package org.oreon.examples.gl.oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.ResourceLoader;
import org.oreon.gl.components.water.Water;

public class WaterWireframeShader extends GLShaderProgram{

private static WaterWireframeShader instance = null;
	

	public static WaterWireframeShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new WaterWireframeShader();
	    }
	      return instance;
	}
	
	protected WaterWireframeShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("shaders/water/water.vert"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/water/water.tesc"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/water/water.tese"));
		addGeometryShader(ResourceLoader.loadShader("shaders/water/water.wireframe.geom"));
		addFragmentShader(ResourceLoader.loadShader("shaders/water/water.wireframe.frag"));
		compileShader();
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		
		addUniform("displacementScale");
		addUniform("choppiness");
		addUniform("texDetail");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("Dy");
		addUniform("Dx");
		addUniform("Dz");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void updateUniforms(Renderable object)
	{
		setUniform("viewProjectionMatrix", BaseContext.getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("eyePosition", BaseContext.getCamera().getPosition());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", BaseContext.getCamera().getFrustumPlanes()[i]);
		}
		
		Water ocean = (Water) object;
		WaterConfiguration configuration = ocean.getWaterConfiguration();
		
		setUniformf("displacementScale", configuration.getDisplacementScale());
		setUniformf("choppiness", configuration.getChoppiness());
		setUniformi("texDetail", configuration.getUvScale());
		setUniformi("tessFactor", configuration.getTessellationFactor());
		setUniformf("tessSlope", configuration.getTessellationSlope());
		setUniformf("tessShift", configuration.getTessellationShift());
		
		glActiveTexture(GL_TEXTURE0);
		ocean.getFft().getDy().bind();
		setUniformi("Dy", 0);
		glActiveTexture(GL_TEXTURE1);
		ocean.getFft().getDx().bind();
		setUniformi("Dx", 1);
		glActiveTexture(GL_TEXTURE2);
		ocean.getFft().getDz().bind();
		setUniformi("Dz", 2);
	}
}
