package engine.gameObject.components;

import engine.core.Camera;
import engine.renderpipeline.RenderingConfig;
import engine.renderpipeline.Shader;
import engine.renderpipeline.data.MeshVAO;

public class MeshRenderer extends Renderer{

	private MeshVAO vao;
	
	public MeshRenderer(MeshVAO meshBuffer, Shader shader, RenderingConfig config)
	{
		super(config, shader);
		this.vao = meshBuffer;
	}
	

	public void render() {
		
		getConfig().enable();
		
		getShader().execute();
		getShader().sendUniforms(getTransform().getWorldMatrix(), Camera.getInstance().getViewProjectionMatrix(), getTransform().getModelViewProjectionMatrix());
		getShader().sendUniforms(((Model) getParent().getComponents().get("Model")).getMaterial());
		
		vao.draw();
		
		getConfig().disable();
		
	}
}
