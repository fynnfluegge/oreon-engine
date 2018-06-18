package org.oreon.examples.vk.oreonworlds;

import java.lang.management.ManagementFactory;

import org.oreon.common.ui.UIElement;
import org.oreon.common.ui.UIScreen;
import org.oreon.core.math.Vec4f;
import org.oreon.core.system.CoreEngine;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.vk.components.ui.VkColorPanel;
import org.oreon.vk.components.ui.VkDynamicTextPanel;
import org.oreon.vk.components.ui.VkGUI;
import org.oreon.vk.components.ui.VkStaticTextPanel;
import org.oreon.vk.components.ui.VkTexturePanel;

public class VkSystemMonitor extends VkGUI {
	
	@SuppressWarnings("restriction")
	private com.sun.management.OperatingSystemMXBean bean;
	
	@SuppressWarnings("restriction")
	@Override
	public void init(VkImageView imageView) {
		super.init(imageView);
		bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		UIScreen screen0 = new UIScreen();
		screen0.setElements(new UIElement[6]);
		screen0.getElements()[0] = new VkColorPanel(new Vec4f(0,0,0,0.5f), 960, 225, 325, 225,
				panelMeshBuffer, guiOverlayFbo);
		screen0.getElements()[1] = new VkStaticTextPanel("FPS:", 1020, 40, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[2] = new VkStaticTextPanel("CPU:", 1020, 90, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[3] = new VkDynamicTextPanel("000", 1120, 40, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[4] = new VkDynamicTextPanel("000", 1120, 90, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[5] = new VkTexturePanel("textures/logo/VulkanLogo.png", 970, 210, 300, 90,
				panelMeshBuffer, guiOverlayFbo);
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
