package org.oreon.modules.gl.postProcessingEffects.lensFlare;

import org.oreon.core.gl.config.AdditiveBlending;
import org.oreon.core.gl.shaders.lensFlare.LensFlareShader;
import org.oreon.modules.gl.gui.elements.TexturePanel;

public class LensFlareTexturePanel extends TexturePanel{

	private float spacing;
	private float transparency;
	
	public LensFlareTexturePanel() {
		super();
		setConfig(new AdditiveBlending());
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
