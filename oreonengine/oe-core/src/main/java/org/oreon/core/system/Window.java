package org.oreon.core.system;

public abstract class Window {
	
	private long id;
	private int width;
	private int height;
	private String title;
	
	public abstract void create();
	
	public abstract void draw();
	
	public abstract void shutdown();
	
	public abstract boolean isCloseRequested();

	public abstract void resize(int x, int y);
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long window) {
		this.id = window;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
