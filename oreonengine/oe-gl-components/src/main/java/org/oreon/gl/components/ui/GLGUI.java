package org.oreon.gl.components.ui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.oreon.common.ui.GUI;
import org.oreon.common.ui.UIPanelLoader;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilter;

public class GLGUI extends GUI{
	
	protected GLTexture fontsTexture;
	protected GUIVAO panelMeshBuffer;
	
	public void init() {
		fontsTexture = new Texture2DBilinearFilter("gui/tex/Fonts.png");
		panelMeshBuffer = new GUIVAO();
		panelMeshBuffer.addData(UIPanelLoader.load("gui/basicPanel.gui"));
	}
	
	@Override
	public void render(){
		glDisable(GL_DEPTH_TEST);
		super.render();
		glEnable(GL_DEPTH_TEST);
	};

}
