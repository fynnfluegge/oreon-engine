package engine.core;

import java.awt.Canvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class OpenGLWindow {
	
		public static void create(int width, int height, String title)
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
		
		public static void embed(int width, int height, Canvas canvas)
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
		
		public static void render()
		{
			Display.update();
		}
		
		public static void dispose()
		{
			Display.destroy();
			Keyboard.destroy();
			Mouse.destroy();
		}
		
		public static boolean isCloseRequested()
		{
			return Display.isCloseRequested();
		}
		
		public static int getWidth()
		{
			return Display.getDisplayMode().getWidth();
		}
		
		public static int getHeight()
		{
			return Display.getDisplayMode().getHeight();
		}
		
		public static String getTitle()
		{
			return Display.getTitle();
		}
}
