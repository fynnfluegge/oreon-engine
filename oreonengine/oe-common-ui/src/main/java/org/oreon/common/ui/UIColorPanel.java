package org.oreon.common.ui;

import org.oreon.core.math.Vec4f;

public class UIColorPanel extends UIElement{

	protected Vec4f rgba;
	
	public UIColorPanel(Vec4f rgba, int xPos, int yPos, int xScaling, int yScaling) {
		super(xPos, yPos, xScaling, yScaling);
		this.rgba = rgba;
		panel = UIPanelLoader.load("gui/basicPanel.gui");
	}

}
