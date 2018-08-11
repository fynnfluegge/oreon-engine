package org.oreon.common.ui;

import org.oreon.core.scenegraph.RenderList;

import lombok.Getter;
import lombok.Setter;

public class UIScreen {
	
	@Getter
	@Setter
	private UIElement[] elements;
	
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
	
	public void record(RenderList renderList){

		for (UIElement element: elements)
		{
			element.record(renderList);
		}
	}
	
	public void shutdown(){

		for (UIElement element: elements)
		{
			element.shutdown();
		}
	}
}
