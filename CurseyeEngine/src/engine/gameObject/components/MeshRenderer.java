package engine.gameObject.components;

import engine.configs.RenderingConfig;
import engine.core.Camera;
import engine.gpubuffers.MeshVAO;
import engine.shaderprograms.Shader;

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
		getShader().sendUniforms(((Material) getParent().getComponents().get("Material")));
		
		vao.draw();
		
		getConfig().disable();
	}
}
