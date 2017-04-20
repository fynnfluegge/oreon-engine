package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import modules.gui.GUI;
import modules.gui.elements.FullScreenTexturePanel;
import modules.lighting.DirectionalLight;
import modules.mousePicking.TerrainPicking;
import modules.postProcessingEffects.DepthOfFieldBlur;
import modules.postProcessingEffects.Bloom;
import modules.postProcessingEffects.MotionBlur;
import modules.shadowmapping.directionalLight.ShadowMaps;
import modules.terrain.Terrain;
import engine.configs.RenderConfig;
import engine.math.Quaternion;
import engine.scenegraph.Scenegraph;
import engine.textures.Texture2D;
import engine.utils.Constants;

public class RenderingEngine {

	private Window window;
	private FullScreenTexturePanel screenTexture;
	private Texture2D postProcessingTexture;
	
	private static Quaternion clipplane;
	private static boolean grid;
	
	private Scenegraph scenegraph;
	private static ShadowMaps shadowMaps;
	private GUI gui;
	
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	
	private static boolean motionBlurEnabled = false;
	private static boolean depthOfFieldBlurEnabled = false;
	private static boolean bloomEnabled = false;
	private static boolean isReflection = false;
	
	
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
		bloom = new Bloom();
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
		
		postProcessingTexture = new Texture2D(window.getSceneTexture());
		
		// post processing effects
		
		// HDR Bloom
		if (isBloomEnabled()) {
			bloom.render(postProcessingTexture);
			postProcessingTexture = bloom.getBloomBlurSceneTexture();
		}
		
		// Depth of Field Blur
		if (isDepthOfFieldBlurEnabled()){
			
			// copy scene texture into low-resolution texture
			dofBlur.getLowResFbo().bind();
			screenTexture.setTexture(postProcessingTexture);
			glViewport(0,0,(int)(window.getWidth()/1.4f),(int)(window.getHeight()/1.4f));
			screenTexture.render();
			dofBlur.getLowResFbo().unbind();
			glViewport(0,0,window.getWidth(),window.getHeight());
			
			dofBlur.render(window.getSceneDepthmap(), postProcessingTexture);
			postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
		}
		
		// Motion Blur
		if (isMotionBlurEnabled()){
			motionBlur.render(window.getSceneDepthmap(), postProcessingTexture);
			postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
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
		if (Camera.getInstance().isCameraMoved()){
			((Terrain) scenegraph.getTerrain()).updateQuadtree();
		}
		DirectionalLight.getInstance().update();
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

	public static boolean isMotionBlurEnabled() {
		return motionBlurEnabled;
	}

	public static void setMotionBlurEnabled(boolean motionBlurEnabled) {
		RenderingEngine.motionBlurEnabled = motionBlurEnabled;
	}

	public static boolean isDepthOfFieldBlurEnabled() {
		return depthOfFieldBlurEnabled;
	}

	public static void setDepthOfFieldBlurEnabled(boolean depthOfFieldBlurEnabled) {
		RenderingEngine.depthOfFieldBlurEnabled = depthOfFieldBlurEnabled;
	}

	public static boolean isBloomEnabled() {
		return bloomEnabled;
	}

	public static void setBloomEnabled(boolean enabled) {
		RenderingEngine.bloomEnabled = enabled;
	}

	public static boolean isReflection() {
		return isReflection;
	}

	public static void setReflection(boolean isReflection) {
		RenderingEngine.isReflection = isReflection;
	}
}
