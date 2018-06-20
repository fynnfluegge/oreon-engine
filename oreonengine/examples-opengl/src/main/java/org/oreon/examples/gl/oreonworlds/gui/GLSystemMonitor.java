package org.oreon.examples.gl.oreonworlds.gui;

import java.lang.management.ManagementFactory;

import org.oreon.common.ui.UIElement;
import org.oreon.common.ui.UIScreen;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Vec4f;
import org.oreon.core.system.CoreEngine;
import org.oreon.gl.components.ui.GLColorPanel;
import org.oreon.gl.components.ui.GLDynamicTextPanel;
import org.oreon.gl.components.ui.GLGUI;
import org.oreon.gl.components.ui.GLStaticTextPanel;
import org.oreon.gl.components.ui.GLTexturePanel;

public class GLSystemMonitor extends GLGUI{

	@SuppressWarnings("restriction")
	private com.sun.management.OperatingSystemMXBean bean;
	
	@SuppressWarnings("restriction")
	
	@Override
	public void init() {
		super.init();
		bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		UIScreen screen0 = new UIScreen();
		screen0.setElements(new UIElement[6]);
		screen0.getElements()[0] = new GLColorPanel(new Vec4f(0,0,0,0.5f), 0,
				EngineContext.getConfig().getWindowHeight()-220, 270, 240, panelMeshBuffer);
		screen0.getElements()[1] = new GLStaticTextPanel("FPS:",
				10, EngineContext.getConfig().getWindowHeight()-40, 40, 40, fontsTexture);
		screen0.getElements()[2] = new GLStaticTextPanel("CPU:", 10,
				EngineContext.getConfig().getWindowHeight()-80, 40, 40, fontsTexture);
		screen0.getElements()[3] = new GLDynamicTextPanel("000", 110,
				EngineContext.getConfig().getWindowHeight()-40, 40, 40, fontsTexture);
		screen0.getElements()[4] = new GLDynamicTextPanel("000", 110,
				EngineContext.getConfig().getWindowHeight()-80, 40, 40, fontsTexture);
		screen0.getElements()[5] = new GLTexturePanel("textures/logo/OpenGLlogo.png", 20,
				EngineContext.getConfig().getWindowHeight()-190, 240, 100, panelMeshBuffer);
		getScreens().add(screen0);
	}
	
	public void update(){
		
		getScreens().get(0).getElements()[3].update(Integer.toString(CoreEngine.getFps()));
		@SuppressWarnings("restriction")
		String cpuLoad = Double.toString(bean.getSystemCpuLoad());
		if (cpuLoad.length() == 3){
			cpuLoad = cpuLoad.substring(2, 3);
		}
		else{
			cpuLoad = cpuLoad.substring(2, 4);
		}
		getScreens().get(0).getElements()[4].update(cpuLoad + "%");
	}
}
