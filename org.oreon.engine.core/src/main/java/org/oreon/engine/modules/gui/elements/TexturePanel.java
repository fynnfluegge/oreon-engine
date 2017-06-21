package org.oreon.engine.modules.gui.elements;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.oreon.engine.modules.gui.GUIElement;
import org.oreon.engine.modules.gui.GUIVAO;
import org.oreon.engine.engine.configs.AlphaTestCullFaceDisable;
import org.oreon.engine.engine.core.Window;
import org.oreon.engine.engine.geometry.Geometrics;
import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.scenegraph.components.Transform;
import org.oreon.engine.engine.shaders.gui.GuiShader;
import org.oreon.engine.engine.textures.Texture2D;

public class TexturePanel extends GUIElement{
	
	private Texture2D texture;
	
	public TexturePanel(){
		
		texture = new Texture2D();
		
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(0, 0, 0);
		getOrthoTransform().setScaling(Window.getInstance().getWidth(), Window.getInstance().getHeight(), 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		
		setShader(GuiShader.getInstance());
		setConfig(new AlphaTestCullFaceDisable(0.0f));
		setVao(new GUIVAO());
		getVao().addData(Geometrics.Quad2D());
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

	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}
}
