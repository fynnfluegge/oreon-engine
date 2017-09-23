package org.oreon.modules.gui.elements;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.texture.Texture2D;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.shaders.gui.GuiShader;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.MeshGenerator;
import org.oreon.modules.gui.GUIElement;
import org.oreon.modules.gui.GUIVAO;

public class TexturePanel extends GUIElement{
	
	private Texture2D texture;
	
	public TexturePanel(){
		
		texture = new Texture2D();
		
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(0, 0, 0);
		getOrthoTransform().setScaling(CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight(), 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		
		setShader(GuiShader.getInstance());
		setConfig(new Default());
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

	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}
}
