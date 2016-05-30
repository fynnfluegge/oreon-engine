package engine.shaders.basic;

import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.scenegraph.components.Material;
import engine.shaders.Shader;

public class TessellationGrid extends Shader{
	
private static TessellationGrid instance = null;
	
	public static TessellationGrid getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TessellationGrid();
	    }
	      return instance;
	}
	
	protected TessellationGrid()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("basic/tessellation grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("basic/tessellation grid/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("basic/tessellation grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("basic/tessellation grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("basic/tessellation grid/Fragment.glsl"));
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
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f viewProjectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		
		setUniform("viewProjectionMatrix", viewProjectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(Material material)
	{	
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
