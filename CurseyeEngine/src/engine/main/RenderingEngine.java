package engine.main;

import java.util.ArrayList;

import modules.gui.GUI;
import modules.gui.elements.FullScreenTexturePanel;
import modules.lighting.DirectionalLight;
import modules.lighting.PointLight;
import modules.vfx.MotionBlur;
import simulations.templates.Simulation;
import engine.configs.RenderingConfig;
import engine.core.Camera;
import engine.core.Constants;
import engine.core.Input;
import engine.core.OpenGLWindow;
import engine.math.Quaternion;

public class RenderingEngine {

	private Simulation simulation;
	private GUI gui;
	private FullScreenTexturePanel screenTexture;
	private static ArrayList<PointLight> lights = new ArrayList<PointLight>();
	private static DirectionalLight directionalLight;
	private static Quaternion clipplane;
	private static boolean grid;
	
	protected MotionBlur motionBlur;
	
	public RenderingEngine(Simulation simulation, GUI gui)
	{
		this.simulation = simulation;
		this.gui = gui;
		screenTexture = new FullScreenTexturePanel();
		motionBlur = new MotionBlur();
	}
	
	public void init()
	{
		screenTexture.init();
		gui.init();
		simulation.init();
		motionBlur.init();
	}
	
	public void render()
	{		
		setClipplane(Constants.PLANE0);
		RenderingConfig.clearScreen();
		simulation.render();
		gui.render();
			
		if (motionBlur.isEnabled()){
			motionBlur.render(simulation.getSceneDepthmap(), simulation.getSceneTexture());
			screenTexture.setTexture(motionBlur.getMotionBlurTexture());
		}
		else 
			screenTexture.setTexture(simulation.getSceneTexture());	

		screenTexture.render();
		
		OpenGLWindow.render();
	}
	
	public void update()
	{
		Input.update();
		Camera.getInstance().input();
		gui.update();
		simulation.update();
		if (motionBlur.isEnabled())
			motionBlur.update();
	}
	
	public void shutdown()
	{
		simulation.shutdown();
	}
	
	public static void addLight(PointLight light)
	{
		RenderingEngine.lights.add(light);
	}

	public static DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public static void setDirectionalLight(DirectionalLight directionalLight) {
		RenderingEngine.directionalLight = directionalLight;
	}

	public static ArrayList<PointLight> getLights() {
		return lights;
	}

	public static void setLights(ArrayList<PointLight> lights) {
		RenderingEngine.lights = lights;
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
}
