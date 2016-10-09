package simulations.oceanSurface.oceanSimulation;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;
import engine.configs.Default;
import engine.core.Transform;
import engine.geometrics.Geometrics;
import engine.main.OpenGLDisplay;
import engine.math.Matrix4f;
import engine.shaders.gui.GuiShader;
import engine.textures.Texture;

public class TexturePanel extends GUIElement{

	static Texture texture;
	
	public void init(){
		
		texture = new Texture();
		setShader(GuiShader.getInstance());
		setConfig(new Default());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(20, 20, 0);
		getOrthoTransform().setScaling(OpenGLDisplay.getInstance().getLwjglWindow().getWidth()/3, OpenGLDisplay.getInstance().getLwjglWindow().getHeight()/3, 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
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

	public Texture getTexture() {
		return texture;
	}
}
