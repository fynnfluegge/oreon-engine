package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import modules.gui.GUI;
import modules.gui.elements.TexturePanel;
import modules.instancing.InstancingObjectHandler;
import modules.lighting.DirectionalLight;
import modules.lighting.LightHandler;
import modules.mousePicking.TerrainPicking;
import modules.postProcessingEffects.DepthOfFieldBlur;
import modules.postProcessingEffects.Bloom;
import modules.postProcessingEffects.MotionBlur;
import modules.postProcessingEffects.SunLightScattering;
import modules.postProcessingEffects.lensFlare.LensFlare;
import modules.shadowmapping.directionalLight.ShadowMaps;
import modules.terrain.Terrain;
import modules.water.UnderWater;
import engine.configs.RenderConfig;
import engine.math.Quaternion;
import engine.scenegraph.Scenegraph;
import engine.textures.Texture2D;
import engine.utils.Constants;

public class RenderingEngine {

	private Window window;
	private TexturePanel fullScreenTexture;
	private Texture2D postProcessingTexture;
	
	private static Quaternion clipplane;
	private static boolean grid;
	
	private Scenegraph scenegraph;
	private InstancingObjectHandler instancingObjectHandler;
	private static ShadowMaps shadowMaps;
	private GUI gui;
	
	private MotionBlur motionBlur;
	private DepthOfFieldBlur dofBlur;
	private Bloom bloom;
	private SunLightScattering sunlightScattering;
	private LensFlare lensFlare;
	private UnderWater underWater;
	
	private static boolean motionBlurEnabled = true;
	private static boolean depthOfFieldBlurEnabled = true;
	private static boolean bloomEnabled = true;
	private static boolean lightScatteringEnabled = true;
	private static boolean waterReflection = false;
	private static boolean waterRefraction = false;
	private static boolean cameraUnderWater = false;
	private static float t_causticsDistortion = 0;
	
	public RenderingEngine(Scenegraph scenegraph, GUI gui)
	{
		window = Window.getInstance();
		instancingObjectHandler = InstancingObjectHandler.getInstance();
		this.scenegraph = scenegraph;
		this.gui = gui;
	}
	
	public void init()
	{
		window.init();
		gui.init();
		
		shadowMaps = new ShadowMaps();
		fullScreenTexture = new TexturePanel();
		motionBlur = new MotionBlur();
		dofBlur = new DepthOfFieldBlur();
		bloom = new Bloom();
		sunlightScattering = new SunLightScattering();
		lensFlare = new LensFlare();
		underWater = UnderWater.getInstance();
	}
	
	public void render()
	{	
		Input.update();
		Camera.getInstance().update();
		
		DirectionalLight.getInstance().update();
		if (Camera.getInstance().isCameraMoved()){
			if (scenegraph.terrainExists()){
				((Terrain) scenegraph.getTerrain()).updateQuadtree();
			}
		}
		
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
		// blit SceneTexture
		window.blitMultisampledFBO(0,0);
		// blit light Scattering SceneTexture
		window.blitMultisampledFBO(1,1);
		
		// start Threads to update instancing objects
		instancingObjectHandler.update();
		
		// start Thread to update Terrain Quadtree
		//TODO Context Sharing
		/*if (Camera.getInstance().isCameraMoved()){
			if (scenegraph.terrainExists()){
				((Terrain) scenegraph.getTerrain()).getLock().lock();;
				try{
					((Terrain) scenegraph.getTerrain()).getCondition().signalAll();
				}
				finally{
					((Terrain) scenegraph.getTerrain()).getLock().unlock();
				}
			}
		}*/
		
		// post processing effects
		
		postProcessingTexture = new Texture2D(window.getSceneTexture());
		
		if (isCameraUnderWater()){
			underWater.render(postProcessingTexture, window.getSceneDepthmap());
			postProcessingTexture = underWater.getUnderwaterSceneTexture();
		}
		
		// HDR Bloom
		if (isBloomEnabled()) {
			bloom.render(postProcessingTexture);
			postProcessingTexture = bloom.getBloomBlurSceneTexture();
		}
		
		// Depth of Field Blur
		if (isDepthOfFieldBlurEnabled()){
			
			// copy scene texture into low-resolution texture
			dofBlur.getLowResFbo().bind();
			fullScreenTexture.setTexture(postProcessingTexture);
			glViewport(0,0,(int)(window.getWidth()/1.4f),(int)(window.getHeight()/1.4f));
			fullScreenTexture.render();
			dofBlur.getLowResFbo().unbind();
			glViewport(0,0,window.getWidth(),window.getHeight());
			
			dofBlur.render(window.getSceneDepthmap(), postProcessingTexture);
			postProcessingTexture = dofBlur.getVerticalBlurSceneTexture();
		}
		
		// Motion Blur
		if (isMotionBlurEnabled()){
			if (Camera.getInstance().getPreviousPosition().sub(Camera.getInstance().getPosition()).length() > 0.1f ||
				Camera.getInstance().getForward().sub(Camera.getInstance().getPreviousForward()).length() > 0.01f){
				motionBlur.render(window.getSceneDepthmap(), postProcessingTexture);
				postProcessingTexture = motionBlur.getMotionBlurSceneTexture();
			}
		}
		
		if (isLightScatteringEnabled() && !isGrid()){
			sunlightScattering.render(postProcessingTexture,window.getBlackScene4LightScatteringTexture());
			postProcessingTexture = sunlightScattering.getSunLightScatteringSceneTexture();
		}

		// final scene texture
		fullScreenTexture.setTexture(postProcessingTexture);	

		fullScreenTexture.render();
		
		window.getFBO().bind();
		LightHandler.doOcclusionQueries();
		window.getFBO().unbind();
		
		lensFlare.render();
		
		gui.render();
		
		// draw into OpenGL window
		window.render();
	}
	
	public void update()
	{
		Input.update();
		Camera.getInstance().update();
		gui.update();
		
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

	public static boolean isWaterReflection() {
		return waterReflection;
	}

	public static void setWaterReflection(boolean isReflection) {
		RenderingEngine.waterReflection = isReflection;
	}

	public static boolean isWaterRefraction() {
		return waterRefraction;
	}

	public static void setWaterRefraction(boolean waterRefraction) {
		RenderingEngine.waterRefraction = waterRefraction;
	}

	public static boolean isLightScatteringEnabled() {
		return lightScatteringEnabled;
	}

	public static void setLightScatteringEnabled(boolean lightScatteringEnabled) {
		RenderingEngine.lightScatteringEnabled = lightScatteringEnabled;
	}
	
	public static boolean isCameraUnderWater() {
		return cameraUnderWater;
	}

	public static void setCameraUnderWater(boolean underWater) {
		RenderingEngine.cameraUnderWater = underWater;
	}

	public static float getT_causticsDistortion() {
		return t_causticsDistortion;
	}
}
