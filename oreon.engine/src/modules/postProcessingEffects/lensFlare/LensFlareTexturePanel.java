package modules.postProcessingEffects.lensFlare;

import engine.configs.AdditiveBlending;
import engine.configs.Default;
import engine.shaders.lensFlare.LensFlareShader;
import modules.gui.elements.TexturePanel;

public class LensFlareTexturePanel extends TexturePanel{

	private float spacing;
	private float transparency;
	
	public LensFlareTexturePanel() {
		super();
		setConfig(new AdditiveBlending(0.0f));
		setShader(LensFlareShader.getInstance());
	}
	
	public void render() {
		
		getConfig().enable();
		getShader().bind();
		getShader().updateUniforms(getOrthographicMatrix());
		getShader().updateUniforms(getTexture(), transparency);
		getVao().draw();
		getConfig().disable();
	}

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public float getTransparency() {
		return transparency;
	}

	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}
}
