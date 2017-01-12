package modules.postProcessingEffects;

import engine.buffers.Framebuffer;
import engine.textures.Texture2D;

public class Bloom {

	private Framebuffer fbo;
	private Texture2D brightColorSceneTexture;
	private Texture2D horizontalBlurBrightColorSceneTexture;
	private Texture2D verticalBlurBrightColorSceneTexture;
	private Texture2D hdrBloomSceneTexture;
	
	
	public Bloom(){
		
	}
	
	public Framebuffer getFbo() {
		return fbo;
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
