package engine.core;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageIOImageData;

public class LwjglWindow {
	
		public void create(int width, int height, String title)
		{
			Display.setTitle(title);
			try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.setIcon(new ByteBuffer[] {
						new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/logo/oreon_lwjgl_icon16.png")), false, false, null),
	                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/logo/oreon_lwjgl_icon32.png")), false, false, null)
	                    });
				Display.create();
				Keyboard.create();
				Mouse.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
