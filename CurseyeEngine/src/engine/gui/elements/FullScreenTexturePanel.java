package engine.gui.elements;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.configs.AlphaCullFaceDisable;
import engine.core.Geometrics;
import engine.core.Texture;
import engine.core.Transform;
import engine.core.OpenGLWindow;
import engine.gui.GUIElement;
import engine.gui.GUIVAO;
import engine.math.Matrix4f;
import engine.shaders.gui.GuiShader;

public class FullScreenTexturePanel extends GUIElement{
	
	private Texture texture;
	
	public FullScreenTexturePanel(){	
	}
	
	public void init(){
		
		texture = new Texture();
		setShader(GuiShader.getInstance());
		setConfig(new AlphaCullFaceDisable(0.0f));
		setOrthographicMatrix(new Matrix4f().Orthographic());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(0, 0, 0);
		getOrthoTransform().setScaling(OpenGLWindow.getWidth(), OpenGLWindow.getHeight(), 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		setVao(new GUIVAO());
		getVao().addData(Geometrics.Quad());
	}
	
	public void render()
	{
		getConfig().enable();
		getShader().execute();
		getShader().sendUniforms(getOrthographicMatrix());
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		getShader().sendUniforms(0);
		getVao().draw();
		getConfig().disable();
	}	

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}	
}
