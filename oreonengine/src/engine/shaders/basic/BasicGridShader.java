package engine.shaders.basic;

import engine.math.Vec3f;
import engine.scene.GameObject;
import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class BasicGridShader extends Shader{

	private static BasicGridShader instance = null;
	
	public static BasicGridShader getInstance() 
	{
		if(instance == null) 
		{
			instance = new BasicGridShader();
		}
		return instance;
	}
		
	protected BasicGridShader()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("shaders/basic/grid/Vertex.glsl"));
		addGeometryShader(ResourceLoader.loadShader("shaders/basic/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("shaders/basic/grid/Fragment.glsl"));
		compileShader();
			
		addUniform("modelViewProjectionMatrix");
		addUniform("color");
	}
		
	public void updateUniforms(GameObject object)
	{
		setUniform("modelViewProjectionMatrix", object.getTransform().getModelViewProjectionMatrix());
		setUniform("color", new Vec3f(0.1f,0.9f,0.1f));
	}
}
