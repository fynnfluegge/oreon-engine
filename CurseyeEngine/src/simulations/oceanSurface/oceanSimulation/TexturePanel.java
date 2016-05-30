package simulations.oceanSurface.oceanSimulation;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;
import engine.configs.AlphaCullFaceDisable;
import engine.core.Geometrics;
import engine.core.Texture;
import engine.core.Transform;
import engine.core.OpenGLWindow;
import engine.math.Matrix4f;
import engine.shaders.gui.GuiShader;

public class TexturePanel extends GUIElement{

	static Texture texture;
	
	public void init(){
		
		texture = new Texture();
		setShader(GuiShader.getInstance());
		setConfig(new AlphaCullFaceDisable(0.0f));
		setOrthographicMatrix(new Matrix4f().Orthographic());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(20, 20, 0);
		getOrthoTransform().setScaling(OpenGLWindow.getWidth()/3, OpenGLWindow.getHeight()/3, 0);
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
}
