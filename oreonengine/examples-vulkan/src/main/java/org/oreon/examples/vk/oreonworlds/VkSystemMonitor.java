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

public class VkSystemMonitor extends VkGUI {
	
	@SuppressWarnings("restriction")
	private com.sun.management.OperatingSystemMXBean bean;
	
	@SuppressWarnings("restriction")
	@Override
	public void init(VkImageView imageView) {
		super.init(imageView);
		bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		UIScreen screen0 = new UIScreen();
		screen0.setElements(new UIElement[5]);
		screen0.getElements()[0] = new VkColorPanel(new Vec4f(0,0,0,0.5f), 0, 95, 210, 120, guiOverlayFbo);
		screen0.getElements()[1] = new VkStaticTextPanel("FPS:", 10, 40, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[2] = new VkStaticTextPanel("CPU:", 10, 90, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[3] = new VkDynamicTextPanel("000", 110, 40, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
		screen0.getElements()[4] = new VkDynamicTextPanel("000", 110, 90, 40, 40,
				fontsImageBundle.getImageView(), fontsImageBundle.getSampler(), guiOverlayFbo);
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
