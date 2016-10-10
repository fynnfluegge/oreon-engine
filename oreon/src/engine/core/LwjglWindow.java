package engine.core;

import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class LwjglWindow {
	
		public void create(int width, int height, String title)
		{
			Display.setTitle(title);
			try {

				Display.setDisplayMode(new DisplayMode(width, height));
				Display.create();
				Keyboard.create();
				Mouse.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
		
		public void embed(int width, int height, Canvas canvas)
		{
			try {
				Display.setParent(canvas);
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.create();
				Keyboard.create();
				Mouse.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
		
		public void render()
		{
			Display.update();
		}
		
		public void dispose()
		{
			Display.destroy();
			Keyboard.destroy();
			Mouse.destroy();
		}
		
		public boolean isCloseRequested()
		{
			return Display.isCloseRequested();
		}
		
		public int getWidth()
		{
			return Display.getDisplayMode().getWidth();
		}
		
		public int getHeight()
		{
			return Display.getDisplayMode().getHeight();
		}
		
		public String getTitle()
		{
			return Display.getTitle();
		}
}
