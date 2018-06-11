package org.oreon.common.ui;

import lombok.Getter;
import lombok.Setter;

public class UIScreen {
	
	@Getter
	@Setter
	private UIElement[] elements;
	
	public void init()
	{
		for (UIElement element: elements)
		{
			element.init();
		}
	}
	
	public void render()
	{
		for (UIElement element: elements)
		{
			element.render();
		}
	}
	
	public void update()
	{
		for (UIElement element: elements)
		{
			element.update();
		}
	}
}
