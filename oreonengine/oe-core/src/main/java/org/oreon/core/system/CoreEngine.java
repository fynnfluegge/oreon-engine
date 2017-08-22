package org.oreon.core.system;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.oreon.core.utils.Constants;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallback;


public class CoreEngine{
	
	private static int fps;
	private static float framerate = 200;
	private static float frameTime = 1.0f/framerate;
	private boolean isRunning;
	private static boolean shareGLContext = false;
	private static Lock glContextLock = new ReentrantLock();
	private static Condition holdGLContext = glContextLock.newCondition();
	private CoreSystem coreSystem;
	
	@SuppressWarnings("unused")
	private GLFWErrorCallback errorCallback;
	
	public void init(CoreSystem coreSystem)
	{
		this.coreSystem = coreSystem;
		
		glfwInit();
		
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		coreSystem.init();
		
		getDeviceProperties();
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
			if(shareGLContext)
			{
				// TODO lwjgl 3 support
//				glContextLock.lock();
//				System.out.println("CoreEngine lock");
//				try{
//
//					Display.releaseContext();
//					System.out.println("CoreEngine signal");
//					holdGLContext.signalAll();
//				} catch (LWJGLException e1) {
//					e1.printStackTrace();
//				}
//				finally{
//					System.out.println("CoreEngine unlock");
//					glContextLock.unlock();
//				}
//				
//				glContextLock.lock();
//				System.out.println("CoreEngine lock");
//				try{
//    				System.out.println("CoreEngine await");
//    				holdGLContext.await();
//		    	} catch (InterruptedException e) {
//		    		e.printStackTrace();
//		    	}
//		    	finally{
//					System.out.println("CoreEngine unlock");
//		    		glContextLock.unlock();
//		    	}
//				
//				try {
//					Display.makeCurrent();
//				} catch (LWJGLException e) {
//					e.printStackTrace();
//				}
//				shareGLContext = false;
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
		coreSystem.getRenderingEngine().shutdown();
		coreSystem.getWindow().dispose();
		glfwTerminate();
	}
	
	private void getDeviceProperties(){
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		System.out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS+ " bytes");
		System.out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS + " bytes");
		System.out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS + " bytes");
		System.out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE + " bytes");
		System.out.println("Max SSBO Block Size: " + GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE + " bytes");		
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
