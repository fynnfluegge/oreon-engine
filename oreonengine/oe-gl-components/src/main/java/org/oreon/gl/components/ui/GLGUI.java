package org.oreon.gl.components.ui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.oreon.common.ui.GUI;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilter;

public class GLGUI extends GUI{
	
	protected GLTexture fontsTexture;
	
	public void init() {
		fontsTexture = new Texture2DBilinearFilter("./res/gui/tex/Fonts.png");
	}
	
	@Override
	public void render(){
		glDisable(GL_DEPTH_TEST);
		super.render();
		glEnable(GL_DEPTH_TEST);
	};

}
