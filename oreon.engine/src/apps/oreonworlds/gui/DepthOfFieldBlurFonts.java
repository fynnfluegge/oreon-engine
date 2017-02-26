package apps.oreonworlds.gui;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import engine.configs.AlphaTestCullFaceDisable;
import engine.core.Window;
import engine.geometry.Geometrics;
import engine.math.Matrix4f;
import engine.scenegraph.components.Transform;
import engine.shader.gui.GuiShader;
import engine.textures.Texture2D;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;

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
	public void init() {
		// TODO Auto-generated method stub
		
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
