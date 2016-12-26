package engine.shader.water;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.water.Water;
import engine.core.Camera;
import engine.scenegraph.GameObject;
import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class OceanGridShader extends Shader{

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
		
		addVertexShader(ResourceLoader.loadShader("shaders/ocean/Ocean_VS.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/ocean/Ocean_TC.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/ocean/Ocean_TE.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/ocean/OceanGrid_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/ocean/OceanGrid_FS.glsl"));
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
		setUniform("viewProjectionMatrix", Camera.getInstance().getViewProjectionMatrix());
		setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
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
