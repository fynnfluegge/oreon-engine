package modules.gui.elements;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.core.Window;
import engine.geometry.Geometrics;
import engine.math.Matrix4f;
import engine.scenegraph.components.Transform;
import engine.shader.gui.GuiShader;
import engine.textures.Texture2D;

public class FullScreenTexturePanel extends GUIElement{
	
	private Texture2D texture;
	
	public FullScreenTexturePanel(){
		
		texture = new Texture2D();
		setShader(GuiShader.getInstance());
		setConfig(new AlphaTestCullFaceDisable(0.0f));
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(0, 0, 0);
		getOrthoTransform().setScaling(Window.getInstance().getWidth(), Window.getInstance().getHeight(), 0);
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

	public Texture2D getTexture() {
		return texture;
	}

	public void setTexture(Texture2D texture) {
		this.texture = texture;
	}


	@Override
	public void init() {	
	}
}
