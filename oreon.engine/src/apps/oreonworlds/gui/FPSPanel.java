package apps.oreonworlds.gui;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import modules.gui.GUIElement;
import modules.gui.GUIObjectLoader;
import modules.gui.GUIVAO;
import engine.configs.AlphaBlending;
import engine.core.CoreEngine;
import engine.core.Window;
import engine.math.Matrix4f;
import engine.math.Vec2f;
import engine.scenegraph.components.Transform;
import engine.shader.gui.GuiShader;
import engine.textures.Texture2D;
import engine.utils.Util;


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
		setConfig(new AlphaBlending(0.3f));
		getVao().addData(GUIObjectLoader.load("fpsPanel.gui"));
		int size = 20;
		setOrthoTransform(new Transform());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		getOrthoTransform().setTranslation(Window.getInstance().getWidth()-75, Window.getInstance().getHeight()-20, 0);
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
