package engine.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import modules.gui.GUI;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import engine.configs.Default;
import engine.scenegraph.Scenegraph;
import engine.utils.Constants;


public class CoreEngine{
	
	private static int fps;
	private static float framerate = 200;
	private static float frameTime = 1.0f/framerate;
	private boolean isRunning;
	private static boolean shareGLContext = false;
	private static Lock glContextLock = new ReentrantLock();
	private static Condition holdGLContext = glContextLock.newCondition();
	private RenderingEngine renderingEngine;
	
	@SuppressWarnings("unused")
	private GLFWErrorCallback errorCallback;
	
	public void createWindow(int width, int height, String title)
	{
		glfwInit();
		
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		Window.getInstance().create(width, height, title);
		getDeviceProperties();
	}
	
	public void init(Scenegraph scenegraph, GUI gui)
	{
		Default.init();
		renderingEngine = new RenderingEngine(scenegraph, gui);
		renderingEngine.init();
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
				
				if(Window.getInstance().isCloseRequested())
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
		renderingEngine.render();
	}
	
	private void update()
	{
		Camera.getInstance().update();
		renderingEngine.update();
	}
	
	private void cleanUp()
	{
		//renderingEngine.shutdown();
		Window.getInstance().dispose();
		glfwTerminate();
	}
	
	private void getDeviceProperties(){
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		System.out.println("Max Geometry Uniform Blocks: " + GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS);
		System.out.println("Max Geometry Shader Invocations: " + GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS);
		System.out.println("Max Uniform Buffer Bindings: " + GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
		System.out.println("Max Uniform Block Size: " + GL31.GL_MAX_UNIFORM_BLOCK_SIZE);
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
