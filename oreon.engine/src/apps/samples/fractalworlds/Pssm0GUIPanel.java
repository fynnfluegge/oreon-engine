package apps.samples.fractalworlds;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.configs.Default;
import engine.core.RenderingEngine;
import engine.geometry.Geometrics;
import engine.math.Matrix4f;
import engine.scenegraph.components.Transform;
import modules.gui.GUIElement;
import modules.gui.GUIVAO;

public class Pssm0GUIPanel extends GUIElement{
	
	public void init(){
		
		setShader(PssmGUIShader.getInstance());
		setConfig(new Default());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(20, 20, 0);
		getOrthoTransform().setScaling(250, 250, 0);
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
		RenderingEngine.getShadowMaps().getDepthMaps().bind();
		getShader().updateUniforms(0,0.0f);
		getVao().draw();
		getConfig().disable();
	}	
}
