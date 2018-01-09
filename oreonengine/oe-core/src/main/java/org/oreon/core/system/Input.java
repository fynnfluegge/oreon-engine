package org.oreon.core.system;

public interface Input {
	
	public void create(long windowId);
	public void update();
	public void shutdown();
	
	public boolean isKeyPushed(int key);
	public boolean isKeyReleased(int key);
	public boolean isButtonPushed(int key);
	public boolean isButtonReleased(int key);
}
