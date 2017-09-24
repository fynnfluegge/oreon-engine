package org.oreon.modules.gui.elements;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.shaders.gui.GuiShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Util;
import org.oreon.modules.gui.GUIElement;
import org.oreon.modules.gui.GUIObjectLoader;
import org.oreon.modules.gui.GUIVAO;

public class FPSPanel extends GUIElement{
	
	private Vec2f[] fps;
	private Texture2D texture;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
	
	public FPSPanel(){
		texture = new Texture2D("./res/gui/tex/Fonts.png");
		texCoords = new Vec2f[24];
		fps = new Vec2f[24];
		setShader(GuiShader.getInstance());
		setVao(new GUIVAO());
		setConfig(new Default());
		getVao().addData(GUIObjectLoader.load("fpsPanel.gui"));
		int size = 20;
		setOrthoTransform(new Transform());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		getOrthoTransform().setTranslation(CoreSystem.getInstance().getWindow().getWidth()-80, CoreSystem.getInstance().getWindow().getHeight()-40, 0);
		getOrthoTransform().setScaling(size, size, 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		Vec2f[] texCoords = new Vec2f[4];
		texCoords = Util.texCoordsFromFontMap('f');
		fps[12] = texCoords[0];
		fps[13] = texCoords[1];
		fps[14] = texCoords[2];
		fps[15] = texCoords[3];
		
		texCoords = Util.texCoordsFromFontMap('p');
		fps[16] = texCoords[0];
		fps[17] = texCoords[1];
		fps[18] = texCoords[2];
		fps[19] = texCoords[3];
		
		texCoords = Util.texCoordsFromFontMap('s');
		fps[20] = texCoords[0];
		fps[21] = texCoords[1];
		fps[22] = texCoords[2];
		fps[23] = texCoords[3];
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
	
	public void update()
	{
		if (CoreEngine.getFps() < 10)
		{
			String chars = String.valueOf(CoreEngine.getFps());
			char zero = '0';
			Vec2f[] texCoords = new Vec2f[4];
			texCoords = Util.texCoordsFromFontMap(zero);
			fps[0] = texCoords[0];
			fps[1] = texCoords[1];
			fps[2] = texCoords[2];
			fps[3] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(zero);
			fps[4] = texCoords[0];
			fps[5] = texCoords[1];
			fps[6] = texCoords[2];
			fps[7] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(0));
			fps[8]  = texCoords[0];
			fps[9]  = texCoords[1];
			fps[10] = texCoords[2];
			fps[11] = texCoords[3];
		}
		else if (CoreEngine.getFps() < 100)
		{
			String chars = String.valueOf(CoreEngine.getFps());
			char zero = '0';
			Vec2f[] texCoords = new Vec2f[4];
			texCoords = Util.texCoordsFromFontMap(zero);
			fps[0] = texCoords[0];
			fps[1] = texCoords[1];
			fps[2] = texCoords[2];
			fps[3] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(0));
			fps[4] = texCoords[0];
			fps[5] = texCoords[1];
			fps[6] = texCoords[2];
			fps[7] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(1));
			fps[8]  = texCoords[0];
			fps[9]  = texCoords[1];
			fps[10] = texCoords[2];
			fps[11] = texCoords[3];
		}
		else
		{
			String chars = String.valueOf(CoreEngine.getFps());
			Vec2f[] texCoords = new Vec2f[4];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(0));
			fps[0] = texCoords[0];
			fps[1] = texCoords[1];
			fps[2] = texCoords[2];
			fps[3] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(1));
			fps[4] = texCoords[0];
			fps[5] = texCoords[1];
			fps[6] = texCoords[2];
			fps[7] = texCoords[3];
			texCoords = Util.texCoordsFromFontMap(chars.charAt(2));
			fps[8]  = texCoords[0];
			fps[9]  = texCoords[1];
			fps[10] = texCoords[2];
			fps[11] = texCoords[3];
		}
		getVao().update(fps);
	}
}
