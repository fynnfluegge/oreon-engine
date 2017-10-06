package org.oreon.modules.gl.water;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;
import org.oreon.modules.gl.water.shader.UnderWaterShader;

public class UnderWater {
	
	private static UnderWater instance = null;
	
	private Texture2D underwaterSceneTexture;
	private UnderWaterShader underWaterShader;
	
	private Texture2D dudvMap;
	private Texture2D causticsMap;
	private float distortion;
	private float distortion_delta = 0.001f;
	
	public static UnderWater getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new UnderWater();
	    }
	      return instance;
	}
	
	protected UnderWater() {
		underWaterShader = UnderWaterShader.getInstance();
		
		underwaterSceneTexture = new Texture2D();
		underwaterSceneTexture.generate();
		underwaterSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						CoreSystem.getInstance().getWindow().getWidth(),
						CoreSystem.getInstance().getWindow().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		underwaterSceneTexture.bilinearFilter();
		underwaterSceneTexture.clampToEdge();
		
		dudvMap = new Texture2D("textures/water/dudv/dudv1.jpg");
		dudvMap.bind();
		dudvMap.trilinearFilter();
		
		causticsMap = new Texture2D("textures/water/caustics/caustics.jpg");
		causticsMap.bind();
		causticsMap.trilinearFilter();
	}
	
	public void render(Texture2D sceneTexture, Texture2D sceneDepthMap) {
		underWaterShader.bind();
		glBindImageTexture(0, sceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, underwaterSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		underWaterShader.updateUniforms(sceneDepthMap);
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/8, CoreSystem.getInstance().getWindow().getHeight()/8, 1);	
		glFinish();
		
		distortion += distortion_delta;
	}

	public Texture2D getUnderwaterSceneTexture() {
		return underwaterSceneTexture;
	}

	public void setUnderwaterSceneTexture(Texture2D underwaterSceneTexture) {
		this.underwaterSceneTexture = underwaterSceneTexture;
	}

	public Texture2D getDudvMap() {
		return dudvMap;
	}

	public void setDudvMap(Texture2D dudvMap) {
		this.dudvMap = dudvMap;
	}

	public Texture2D getCausticsMap() {
		return causticsMap;
	}

	public void setCausticsMap(Texture2D causticsMap) {
		this.causticsMap = causticsMap;
	}

	public float getDistortion() {
		return distortion;
	}

	public void setDistortion(float distortion) {
		this.distortion = distortion;
	}
}
