package org.oreon.core.system;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.oreon.core.util.Constants;

public class CoreEngine{
	
	private static int fps;
	private static float framerate = 200;
	private static float frameTime = 1.0f/framerate;
	private boolean isRunning;
	private static boolean shareGLContext = false;
	private static Lock glContextLock = new ReentrantLock();
	private static Condition holdGLContext = glContextLock.newCondition();
	private CoreSystem coreSystem;
	
	public void init(CoreSystem coreSystem)
	{
		this.coreSystem = coreSystem;
		
		coreSystem.init();
	}
	
	public void start()
	{
		if(isRunning)
			return;
		
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
				
				if(coreSystem.getWindow().isCloseRequested())
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

	private void stop()
	{
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	private void render()
	{
		coreSystem.getRenderingEngine().render();
	}
	
	private void update()
	{
		coreSystem.getInput().update();
		CoreSystem.getInstance().getScenegraph().getCamera().update();
		
		coreSystem.getRenderingEngine().update();
	}
	
	private void cleanUp()
	{
		coreSystem.getWindow().dispose();
		coreSystem.getRenderingEngine().shutdown();
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
}
