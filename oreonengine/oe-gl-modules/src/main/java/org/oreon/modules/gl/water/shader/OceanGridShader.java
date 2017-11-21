package org.oreon.modules.gl.water.shader;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.ResourceLoader;
import org.oreon.modules.gl.water.Water;

public class OceanGridShader extends GLShader{

private static OceanGridShader instance = null;
	

	public static OceanGridShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new OceanGridShader();
	    }
	      return instance;
	}
	
	protected OceanGridShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/Ocean_Shader/Ocean_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/shaders/Ocean_Shader/Ocean_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/shaders/Ocean_Shader/Ocean_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/Ocean_Shader/OceanGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/Ocean_Shader/OceanGrid_FS.glsl"));
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
	
	public void updateUniforms(GameObject object)
	{
		setUniform("viewProjectionMatrix", CoreSystem.getInstance().getScenegraph().getCamera().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
		setUniform("eyePosition", CoreSystem.getInstance().getScenegraph().getCamera().getPosition());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", CoreSystem.getInstance().getScenegraph().getCamera().getFrustumPlanes()[i]);
		}
		
		Water ocean = (Water) object;
		setUniformf("displacementScale", ocean.getDisplacementScale());
		setUniformf("choppiness", ocean.getChoppiness());
		setUniformi("texDetail", ocean.getTexDetail());
		setUniformi("tessFactor", ocean.getTessellationFactor());
		setUniformf("tessSlope", ocean.getTessellationSlope());
		setUniformf("tessShift", ocean.getTessellationShift());
		
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
