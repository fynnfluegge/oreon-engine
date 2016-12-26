package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import modules.gui.GUI;
import modules.gui.elements.FullScreenTexturePanel;
import modules.lighting.DirectionalLight;
import modules.mousePicking.TerrainPicking;
import modules.postProcessingEffects.DepthOfFieldBlur;
import modules.postProcessingEffects.MotionBlur;
import modules.shadowmapping.directionalLights.ShadowMaps;
import engine.configs.RenderConfig;
import engine.math.Quaternion;
import engine.scenegraph.Scenegraph;
import engine.texturing.Texture;
import engine.utils.Constants;

public class RenderingEngine {

	private Window window;
	private FullScreenTexturePanel screenTexture;
	private Texture postProcessingTexture;
	
	private static Quaternion clipplane;
	private static boolean grid;
	
	private Scenegraph scenegraph;
	private static ShadowMaps shadowMaps;
	private GUI gui;
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	
	public RenderingEngine(Scenegraph scenegraph, GUI gui)
	{
		window = Window.getInstance();
		this.scenegraph = scenegraph;
		this.gui = gui;
	}
	
	public void init()
	{
		window.init();
		gui.init();
		
		shadowMaps = new ShadowMaps();
		screenTexture = new FullScreenTexturePanel();
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
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
		window.getMultisampledFbo().bind();
		RenderConfig.clearScreen();
		scenegraph.render();	
		window.getMultisampledFbo().unbind();
		window.getFBO().bind();
		RenderConfig.clearScreen();
		window.getFBO().unbind();
		window.blitMultisampledFBO();
		
		postProcessingTexture = new Texture(window.getSceneTexture());
		
		// post processing
		if (dofBlur.isEnabled()){
			dofBlur.render(window.getSceneDepthmap(), postProcessingTexture);
			postProcessingTexture = dofBlur.getDofBlurTexture();
		}
		
		if (motionBlur.isEnabled()){
			motionBlur.render(window.getSceneDepthmap(), postProcessingTexture);
			postProcessingTexture = motionBlur.getMotionBlurTexture();
		}
		
		// final scene texture
		screenTexture.setTexture(postProcessingTexture);	

		gui.render();
		screenTexture.render();

		window.render();
	}
	
	public void update()
	{
		Input.update();
		Camera.getInstance().update();
		gui.update();
		scenegraph.update();
		DirectionalLight.getInstance().update();
		motionBlur.update();
		TerrainPicking.getInstance().getTerrainPosition();
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
