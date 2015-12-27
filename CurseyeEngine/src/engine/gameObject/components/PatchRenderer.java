package engine.gameObject.components;

import engine.buffers.PatchVAO;
import engine.configs.RenderingConfig;
import engine.core.Camera;
import engine.shaderprograms.Shader;

public class PatchRenderer extends Renderer{

		private PatchVAO vao;
		
		public PatchRenderer(PatchVAO meshBuffer, Shader shader, RenderingConfig config)
		{
			super(config, shader);
			vao = meshBuffer;
		}
		

		public void render() {
			
			getConfig().enable();
			getShader().execute();
			getShader().sendUniforms(getTransform().getWorldMatrix(), Camera.getInstance().getViewProjectionMatrix(), getTransform().getModelViewProjectionMatrix());
			
			// TODO improvement: send rendering data strucure
			getShader().sendUniforms(getParent());
			getShader().sendUniforms(((Model) getParent().getComponents().get("Model")).getMaterial());
			vao.draw();
			getConfig().disable();	
		}


		public PatchVAO getVao() {
			return vao;
		}
}
