package org.oreon.examples.gl.oreonworlds.gui;

import java.lang.management.ManagementFactory;

import org.oreon.common.ui.UIScreen;
import org.oreon.core.CoreEngine;
import org.oreon.core.context.BaseContext;
import org.oreon.core.math.Vec4f;
import org.oreon.gl.components.ui.GLColorPanel;
import org.oreon.gl.components.ui.GLDynamicTextPanel;
import org.oreon.gl.components.ui.GLGUI;
import org.oreon.gl.components.ui.GLStaticTextPanel;

public class GLSystemMonitor extends GLGUI{

	@SuppressWarnings("restriction")
	private com.sun.management.OperatingSystemMXBean bean;
	
	@SuppressWarnings("restriction")
	
	public void init() {
		super.init();
		bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		UIScreen screen0 = new UIScreen();
		screen0.getElements().add(new GLColorPanel(new Vec4f(0,0,0,0.5f), 0,
				BaseContext.getConfig().getWindowHeight()-85, 220, 250, panelMeshBuffer));
		screen0.getElements().add(new GLStaticTextPanel("FPS:",
				10, BaseContext.getConfig().getWindowHeight()-40, 40, 40, fontsTexture));
		screen0.getElements().add(new GLStaticTextPanel("CPU:", 10,
				BaseContext.getConfig().getWindowHeight()-80, 40, 40, fontsTexture));
		screen0.getElements().add(new GLDynamicTextPanel("000", 110,
				BaseContext.getConfig().getWindowHeight()-40, 40, 40, fontsTexture));
		screen0.getElements().add(new GLDynamicTextPanel("000", 110,
				BaseContext.getConfig().getWindowHeight()-80, 40, 40, fontsTexture));
//		screen0.getElements().add(new GLTexturePanel("textures/logo/OpenGL_Logo.png", 0,
//				BaseContext.getConfig().getWindowHeight()-240, 360, 160, panelMeshBuffer));
		getScreens().add(screen0);
	}
	
	public void update(){
		
		getScreens().get(0).getElements().get(3).update(Integer.toString(CoreEngine.getFps()));
		@SuppressWarnings("restriction")
		String cpuLoad = Double.toString(bean.getSystemCpuLoad());
		if (cpuLoad.length() == 3){
			cpuLoad = cpuLoad.substring(2, 3);
		}
		else{
			cpuLoad = cpuLoad.substring(2, 4);
		}
		getScreens().get(0).getElements().get(4).update(cpuLoad + "%");
	}
}
