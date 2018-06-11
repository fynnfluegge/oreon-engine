package org.oreon.gl.components.ui;

public class Screen {
	
	private GUIElement[] elements;

	
	public GUIElement[] getElements() {
		return elements;
	}

	public void setElements(GUIElement[] elements) {
		this.elements = elements;
	}
	
	public void init()
	{
		for (GUIElement element: elements)
		{
			element.init();
		}
	}
	
	public void render()
	{
		for (GUIElement element: elements)
		{
			element.render();
		}
	}
	
	public void update()
	{
		for (GUIElement element: elements)
		{
			element.update();
		}
	}
}
