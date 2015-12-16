package simulations.templates;


import engine.core.Texture;
import engine.gameObject.GameObject;
import engine.renderpipeline.data.Framebuffer;

public abstract class Simulation {

private GameObject rootObject;
private Framebuffer sceneFBO;
private Texture sceneTexture;
private Texture sceneDepthmap;
	
	public void init() {
		
		setRoot(new GameObject());
		setSceneFBO(new Framebuffer());
	}
	
	public void render(){};

	public void update(){};
	
	public void shutdown()
	{
		rootObject.shutdown();
	}

	public GameObject getRoot() {
		return rootObject;
	}

	public void setRoot(GameObject root) {
		this.rootObject = root;
	}

	public Texture getSceneTexture() {
		return sceneTexture;
	}

	public void setSceneTexture(Texture sceneTexture) {
		this.sceneTexture = sceneTexture;
	}

	public Texture getSceneDepthmap() {
		return sceneDepthmap;
	}

	public void setSceneDepthmap(Texture sceneDepthmap) {
		this.sceneDepthmap = sceneDepthmap;
	}

	public Framebuffer getSceneFBO() {
		return sceneFBO;
	}

	public void setSceneFBO(Framebuffer sceneFBO) {
		this.sceneFBO = sceneFBO;
	}
}
