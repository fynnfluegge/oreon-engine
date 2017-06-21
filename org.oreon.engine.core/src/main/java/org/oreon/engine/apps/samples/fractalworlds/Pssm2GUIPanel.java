package org.oreon.engine.apps.samples.fractalworlds;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.oreon.engine.modules.gui.GUIElement;
import org.oreon.engine.modules.gui.GUIVAO;
import org.oreon.engine.engine.configs.Default;
import org.oreon.engine.engine.core.RenderingEngine;
import org.oreon.engine.engine.geometry.Geometrics;
import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.scenegraph.components.Transform;

public class Pssm2GUIPanel extends GUIElement{
	
	public void init(){
		
		setShader(PssmGUIShader.getInstance());
		setConfig(new Default());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthoTransform(new Transform());
		getOrthoTransform().setTranslation(560, 20, 0);
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
		getShader().updateUniforms(0,1.5f);
		getVao().draw();
		getConfig().disable();
	}	
}
