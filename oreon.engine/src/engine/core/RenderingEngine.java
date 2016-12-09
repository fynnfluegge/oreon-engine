package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import modules.gui.GUI;
import modules.gui.elements.FullScreenTexturePanel;
import modules.lighting.DirectionalLight;
import modules.postProcessingEffects.MotionBlur;
import modules.shadowmapping.directionalLights.ShadowMaps;
import engine.configs.RenderConfig;
import engine.math.Quaternion;
import engine.scenegraph.Scenegraph;
import engine.utils.Constants;

public class RenderingEngine {

	private OpenGLDisplay display;
	private FullScreenTexturePanel screenTexture;
	
	private static Quaternion clipplane;
	private static boolean grid;
	
	private Scenegraph scenegraph;
	private static ShadowMaps shadowMaps;
	private GUI gui;
	private MotionBlur motionBlur;
	
	public RenderingEngine(Scenegraph scenegraph, GUI gui)
	{
		display = OpenGLDisplay.getInstance();
		this.scenegraph = scenegraph;
		this.gui = gui;
	}
	
	public void init()
	{
		display.init();
		gui.init();
		
		shadowMaps = new ShadowMaps();
		screenTexture = new FullScreenTexturePanel();
		motionBlur = new MotionBlur();
	}
	
	public void render()
	{		
		setClipplane(Constants.PLANE0);
		RenderConfig.clearScreen();

		// render shadow maps
		shadowMaps.getFBO().bind();
		glClear(GL_DEPTH_BUFFER_BIT);
		scenegraph.renderShadows();
		shadowMaps.getFBO().unbind();
		
		// render scene/deferred maps
		OpenGLDisplay.getInstance().getFBO().bind();
		RenderConfig.clearScreen();
		scenegraph.render();	
		OpenGLDisplay.getInstance().getFBO().unbind();
		
		gui.render();
		
		if (motionBlur.isEnabled()){
			motionBlur.render(display.getSceneDepthmap(), display.getSceneTexture());
			screenTexture.setTexture(motionBlur.getMotionBlurTexture());
		}
		else 
			screenTexture.setTexture(display.getSceneTexture());	

		screenTexture.render();
		
		display.getLwjglWindow().render();
	}
	
	public void update()
	{
		Input.update();
		Camera.getInstance().update();
		gui.update();
		scenegraph.update();
		DirectionalLight.getInstance().update();
		motionBlur.update();
	}
	
	public void shutdown()
	{
		scenegraph.shutdown();
	}

	public static Quaternion getClipplane() {
		return clipplane;
	}

	public static void setClipplane(Quaternion clipplane) {
		RenderingEngine.clipplane = clipplane;
	} 

	public static boolean isGrid() {
		return grid;
	}

	public static void setGrid(boolean grid) {
		RenderingEngine.grid = grid;
	}

	public Scenegraph getScengraph() {
		return scenegraph;
	}

	public void setScengraph(Scenegraph scengraph) {
		this.scenegraph = scengraph;
	}
	
	public static ShadowMaps getShadowMaps() {
		return shadowMaps;
	}

	public static void setShadowMaps(ShadowMaps shadowMaps) {
		RenderingEngine.shadowMaps = shadowMaps;
	}
}
