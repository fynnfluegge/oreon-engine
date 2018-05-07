package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DTrilinearFilter;

import lombok.Getter;

public class UnderWaterRenderer {
	
	@Getter
	private GLTexture underwaterSceneTexture;
	private UnderWaterShader underWaterShader;
	
	private GLTexture dudvMap;
	private GLTexture causticsMap;
	private float distortion;
	private float distortion_delta = 0.001f;
	
	public UnderWaterRenderer() {
		underWaterShader = UnderWaterShader.getInstance();
		
		underwaterSceneTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		underwaterSceneTexture.bind();
		underwaterSceneTexture.clampToEdge();
		underwaterSceneTexture.unbind();
		
		dudvMap = new Texture2DTrilinearFilter("textures/water/dudv/dudv1.jpg");
		causticsMap = new Texture2DTrilinearFilter("textures/water/caustics/caustics.jpg");
		
		GLContext.getRenderContext().setUnderwaterCausticsMap(causticsMap);
		GLContext.getRenderContext().setUnderwaterDudvMap(dudvMap);
	}
	
	public void render(GLTexture sceneTexture, GLTexture sceneDepthMap) {
		
		underWaterShader.bind();
		glBindImageTexture(0, sceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, underwaterSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		underWaterShader.updateUniforms(sceneDepthMap);
		glDispatchCompute(EngineContext.getWindow().getWidth()/8, EngineContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		distortion += distortion_delta;
		
		GLContext.getRenderContext().setUnderwaterDistortion(distortion);
	}

}
