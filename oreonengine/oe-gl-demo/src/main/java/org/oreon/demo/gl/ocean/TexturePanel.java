package org.oreon.demo.gl.ocean;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.shaders.gui.GuiShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.util.MeshGenerator;
import org.oreon.modules.gui.GUIElement;
import org.oreon.modules.gui.GUIVAO;

public class TexturePanel extends GUIElement{

	private static Texture2D texture;
	
	public TexturePanel() {
		texture = new Texture2D();
		setShader(GuiShader.getInstance());
		setConfig(new Default());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(0, 0, 0);
		getOrthoTransform().setScaling(512, 512, 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		setVao(new GUIVAO());
		getVao().addData(MeshGenerator.Quad2D());
	}
	
	public void render()
	{
		getConfig().enable();
		getShader().bind();
		getShader().updateUniforms(getOrthographicMatrix());
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		getShader().updateUniforms(0);
		getVao().draw();
		getConfig().disable();
	}	

	public Texture2D getTexture() {
		return texture;
	}

	public static void setTexture(Texture2D texture) {
		TexturePanel.texture = texture;
	}
}
