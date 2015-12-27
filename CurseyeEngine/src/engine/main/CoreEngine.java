package engine.main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import simulations.templates.Simulation;
import engine.configs.RenderingConfig;
import engine.core.Constants;
import engine.core.Window;
import engine.gui.GUI;


public class CoreEngine{
	
	private int width;
	private int height;
	private String title;
	private static int fps;
	private static float framerate = 100;
	private static float frameTime = 1.0f/framerate;
	private boolean isRunning;
	private RenderingEngine renderingEngine;
	
	private static boolean shareGLContext = false;
	private static boolean glContextfree = false;
	private static Lock glContextLock = new ReentrantLock();
	private static Condition holdGLContext = glContextLock.newCondition();

	public CoreEngine(int width, int height, String title, Simulation simulation, GUI gui)
	{
		this.width = width;
		this.height = height;
		this.title = title;
		isRunning = false;
		renderingEngine = new RenderingEngine(simulation,gui);
	}
	
	public void createWindow()
	{
		Window.createWindow(this.width, this.height, this.title);
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
	}
	
	public void start()
	{
		if(isRunning)
			return;
		
		// init Graphics
		RenderingConfig.init();
		renderingEngine.init();
		
		run();
	}
	
	public void run() {
		
		this.isRunning = true;
		
		int frames = 0;
		long frameCounter = 0;
		
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;
		
		// Rendering Loop
		while(isRunning)
		{
			if(shareGLContext)
			{
				glContextLock.lock();
				try{
					try {
						Display.releaseContext();
						glContextfree = true;
						holdGLContext.signalAll();
					} catch (LWJGLException e1) {
						e1.printStackTrace();
					}
				}
				finally{
					glContextLock.unlock();
				}
				
				glContextLock.lock();
				try{
		    		while(CoreEngine.isGlContextfree())
		    		{
		    			try {
		    				holdGLContext.await();
		    			} catch (InterruptedException e) {
		    				e.printStackTrace();
		    			}
		    		}
		    	}
		    	finally{
		    		glContextLock.unlock();
		    	}
				
				try {
					Display.makeCurrent();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				shareGLContext = false;
			}
			
			boolean render = false;
			
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;
			
			unprocessedTime += passedTime / (double) Constants.NANOSECOND;
			frameCounter += passedTime;
		
			
			while(unprocessedTime > frameTime)
			{

				render = true;
				unprocessedTime -= frameTime;
				
				if(Window.isCloseRequested())
					stop();
				
				update();
				
				if(frameCounter >= Constants.NANOSECOND)
				{
					setFps(frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			if(render)
			{
				render();
				frames++;
			}
			else
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
				
		}
		
		cleanUp();
		
	}

	public void stop()
	{
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	public void render()
	{
		renderingEngine.render();
	}
	
	public void update()
	{
		renderingEngine.update();
	}
	
	public void cleanUp()
	{
		renderingEngine.shutdown();
		Window.dispose();
	}

	public static float getFrameTime() {
		return frameTime;
	}

	public static int getFps() {
		return fps;
	}

	public static void setFps(int fps) {
		CoreEngine.fps = fps;
	}

	public static boolean isShareGLContext() {
		return shareGLContext;
	}

	public static void setShareGLContext(boolean shareGLContext) {
		CoreEngine.shareGLContext = shareGLContext;
	}

	public static Lock getGLContextLock() {
		return glContextLock;
	}

	public static Condition getHoldGLContext() {
		return holdGLContext;
	}

	public static boolean isGlContextfree() {
		return glContextfree;
	}

	public static void setGlContextfree(boolean glContextfree) {
		CoreEngine.glContextfree = glContextfree;
	}
}
