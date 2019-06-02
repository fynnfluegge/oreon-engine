package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

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
		
		underwaterSceneTexture = new TextureImage2D(BaseContext.getWindow().getWidth(),
				BaseContext.getWindow().getHeight(),
				ImageFormat.RGBA16FLOAT, SamplerFilter.Bilinear, TextureWrapMode.ClampToEdge);
		
		dudvMap = new TextureImage2D("textures/water/dudv/dudv1.jpg", SamplerFilter.Trilinear);
		causticsMap = new TextureImage2D("textures/water/caustics/caustics.jpg", SamplerFilter.Trilinear);
		
		GLContext.getResources().setUnderwaterCausticsMap(causticsMap);
		GLContext.getResources().setUnderwaterDudvMap(dudvMap);
	}
	
	public void render(GLTexture sceneTexture, GLTexture sceneDepthMap) {
		
		underWaterShader.bind();
		glBindImageTexture(0, sceneTexture.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, underwaterSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		underWaterShader.updateUniforms(sceneDepthMap);
		glDispatchCompute(BaseContext.getWindow().getWidth()/8, BaseContext.getWindow().getHeight()/8, 1);	
		glFinish();
		
		distortion += distortion_delta;
		
		GLContext.getResources().getWaterConfig().setUnderwaterDistortion(distortion);
	}

}
