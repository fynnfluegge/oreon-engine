package samples.fractalworlds;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;
import engine.configs.Default;
import engine.core.Transform;
import engine.geometry.Geometrics;
import engine.main.OpenGLDisplay;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;

public class Pssm2GUIPanel extends GUIElement{
	
	public void init(){
		
		setShader(PssmGUIShader.getInstance());
		setConfig(new Default());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(380, 20, 0);
		getOrthoTransform().setScaling(OpenGLDisplay.getInstance().getLwjglWindow().getWidth()/5, OpenGLDisplay.getInstance().getLwjglWindow().getHeight()/5, 0);
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
		RenderingEngine.getShadowMaps().getDepthMaps().bind2DArray();
		getShader().updateUniforms(0,1.5f);
		getVao().draw();
		getConfig().disable();
	}	
}
