package engine.shader.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.utils.ResourceLoader;

public class BasicTessellationGridShader extends Shader{
	
private static BasicTessellationGridShader instance = null;
	
	public static BasicTessellationGridShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BasicTessellationGridShader();
	    }
	      return instance;
	}
	
	protected BasicTessellationGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/tessellation grid/Fragment.glsl"));
		compileShader();
		
		
		addUniform("viewProjectionMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		
		
		addUniform("material.displacemap");
		addUniform("material.displaceScale");
		addUniform("displacement");
		
		addUniform("clipplane");
		
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
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
		
		Material material = (Material) object.getComponents().get("Material");
		
		setUniformi("tessFactor", 10000);
		setUniformf("tessSlope", 1.4f);
		setUniformf("tessShift", 1.0f);
			
		if (material.getDisplacemap() != null){
			setUniformi("displacement", 1);
			glActiveTexture(GL_TEXTURE3);
			material.getDisplacemap().bind();
			setUniformi("material.displacemap", 3);
			setUniformf("material.displaceScale", material.getDisplaceScale());
		}
		else
			setUniformi("displacement", 0);
	}
}
