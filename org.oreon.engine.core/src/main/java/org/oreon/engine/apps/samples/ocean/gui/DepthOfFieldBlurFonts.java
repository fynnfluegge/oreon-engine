package org.oreon.engine.apps.samples.ocean.gui;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.engine.engine.configs.AlphaTestCullFaceDisable;
import org.oreon.engine.engine.core.Window;
import org.oreon.engine.engine.geometry.Geometrics;
import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.scenegraph.components.Transform;
import org.oreon.engine.engine.shaders.gui.GuiShader;
import org.oreon.engine.engine.textures.Texture2D;
import org.oreon.engine.modules.gui.GUIElement;
import org.oreon.engine.modules.gui.GUIVAO;

public class DepthOfFieldBlurFonts extends GUIElement{

	private Texture2D texture;
	
	public DepthOfFieldBlurFonts() {
		
		texture = new Texture2D("./res/gui/tex/DepthofFieldBlurtxt.png");
		setShader(GuiShader.getInstance());
		setConfig(new AlphaTestCullFaceDisable(0.0f));
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(7, Window.getInstance().getHeight()-160, 0);
		getOrthoTransform().setScaling(240, -30, 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		setVao(new GUIVAO());
		getVao().addData(Geometrics.Quad2D());
	}

	@Override
	public void render() {
	
		getConfig().enable();
		getShader().bind();
		getShader().updateUniforms(getOrthographicMatrix());
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		getShader().updateUniforms(0);
		getVao().draw();
		getConfig().disable();
	}

}
