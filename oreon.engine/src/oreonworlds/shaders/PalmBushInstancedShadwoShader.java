package oreonworlds.shaders;

import engine.core.Constants;
import engine.scenegraph.GameObject;
import engine.shadersamples.Shader;
import engine.utils.ResourceLoader;

public class PalmBushInstancedShadwoShader extends Shader{

		private static PalmBushInstancedShadwoShader instance;

		public static PalmBushInstancedShadwoShader getInstance() 
		{
		    if(instance == null) 
		    {
		    	instance = new PalmBushInstancedShadwoShader();
		    }
		     return instance;
		}
		
		protected PalmBushInstancedShadwoShader()
		{
			super();
			
			addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBush_VS.glsl"));
			addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBushShadow_GS.glsl"));
			addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/PalmBush/PalmBushShadow_FS.glsl"));
			compileShader();
			
			addUniform("worldMatrix");
			
			addUniformBlock("Camera");
			addUniformBlock("LightViewProjections");
		}
		
		public void updateUniforms(GameObject object){
			
			bindUniformBlock("Camera",Constants.CameraUniformBlockBinding);
			bindUniformBlock("LightViewProjections",Constants.LightMatricesUniformBlockBinding);
			setUniform("worldMatrix", object.getTransform().getWorldMatrix());
		}
}
