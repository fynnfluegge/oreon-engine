package org.oreon.system.vk.desktop;

import static org.lwjgl.glfw.GLFW.glfwInit;

import org.oreon.core.system.Window;

public class VKWindow extends Window{

	@Override
	public void create() {
		
		glfwInit();
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCloseRequested() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resize(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
