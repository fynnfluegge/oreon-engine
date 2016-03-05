package engine.main;


import java.util.ArrayList;

import simulations.templates.Simulation;
import engine.configs.RenderingConfig;
import engine.core.Camera;
import engine.core.Constants;
import engine.core.Input;
import engine.core.Window;
import engine.gui.GUI;
import engine.gui.elements.FullScreenTexturePanel;
import engine.lighting.DirectionalLight;
import engine.lighting.PointLight;
import engine.math.Quaternion;
import engine.vfx.motionBlur.MotionBlur;

public class RenderingEngine {

	private Simulation simulation;
	private GUI gui;
	private FullScreenTexturePanel screenTexture;
	private static ArrayList<PointLight> lights = new ArrayList<PointLight>();
	private static DirectionalLight directionalLight;
	private static Quaternion clipplane;
	private static boolean grid;
	private MotionBlur motionBlur;
	
	public RenderingEngine(Simulation simulation, GUI gui)
	{
		this.simulation = simulation;
		this.gui = gui;
		screenTexture = new FullScreenTexturePanel();
	}
	
	public void init()
	{
		screenTexture.init();
		gui.init();
		simulation.init();
		motionBlur = new MotionBlur();
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
			screenTexture.render();
		}
		else {
			screenTexture.setTexture(simulation.getSceneTexture());
			screenTexture.render();
		}	
		Window.render();
	}
	
	public void update()
	{
		Input.update();
		Camera.getInstance().input();
		gui.update();
		simulation.update();
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
