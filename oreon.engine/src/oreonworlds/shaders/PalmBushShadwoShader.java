package oreonworlds.shaders;

import engine.core.Constants;
import engine.scenegraph.GameObject;
import engine.shadersamples.Shader;
import engine.utils.ResourceLoader;

public class PalmBushShadwoShader extends Shader{

		private static PalmBushShadwoShader instance;

		public static PalmBushShadwoShader getInstance() 
		{
		    if(instance == null) 
		    {
		    	instance = new PalmBushShadwoShader();
		    }
		     return instance;
		}
		
		protected PalmBushShadwoShader()
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
