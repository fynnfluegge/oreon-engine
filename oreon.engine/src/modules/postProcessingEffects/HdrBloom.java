package modules.postProcessingEffects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;

import java.nio.ByteBuffer;

import engine.core.Window;
import engine.textures.Texture2D;

public class HdrBloom {

	private Texture2D brightColorSceneTexture;
	private Texture2D horizontalBlurBrightColorSceneTexture;
	private Texture2D verticalBlurBrightColorSceneTexture;
	private Texture2D hdrBloomSceneTexture;
	
	
	public HdrBloom(){
		
		brightColorSceneTexture = new Texture2D();
		brightColorSceneTexture.generate();
		brightColorSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		brightColorSceneTexture.bilinearFilter();
		brightColorSceneTexture.clampToEdge();
		
		horizontalBlurBrightColorSceneTexture = new Texture2D();
		horizontalBlurBrightColorSceneTexture.generate();
		horizontalBlurBrightColorSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		horizontalBlurBrightColorSceneTexture.bilinearFilter();
		horizontalBlurBrightColorSceneTexture.clampToEdge();
		
		verticalBlurBrightColorSceneTexture = new Texture2D();
		verticalBlurBrightColorSceneTexture.generate();
		verticalBlurBrightColorSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		verticalBlurBrightColorSceneTexture.bilinearFilter();
		verticalBlurBrightColorSceneTexture.clampToEdge();
		
		hdrBloomSceneTexture = new Texture2D();
		hdrBloomSceneTexture.generate();
		hdrBloomSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
						Window.getInstance().getWidth(),
						Window.getInstance().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		hdrBloomSceneTexture.bilinearFilter();
		hdrBloomSceneTexture.clampToEdge();
	}
	
	public void render(Texture2D sceneSampler) {
		
	}
	
	public Texture2D getBrightColorSceneTexture() {
		return brightColorSceneTexture;
	}

	public Texture2D getHorizontalBlurBrightColorSceneTexture() {
		return horizontalBlurBrightColorSceneTexture;
	}

	public Texture2D getVerticalBlurBrightColorSceneTexture() {
		return verticalBlurBrightColorSceneTexture;
	}

	public Texture2D getHdrBloomSceneTexture() {
		return hdrBloomSceneTexture;
	}
}
