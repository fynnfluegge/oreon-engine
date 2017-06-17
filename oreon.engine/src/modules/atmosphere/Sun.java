package modules.atmosphere;

import engine.buffers.PointVAO3D;
import engine.configs.Default;
import engine.core.RenderingEngine;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.textures.Texture2D;
import modules.lighting.DirectionalLight;
import modules.lighting.Light;
import modules.lighting.LightHandler;

public class Sun extends GameObject{
	
	public Sun(){
		
		getTransform().setLocalTranslation(DirectionalLight.getInstance().getDirection().mul(-2800));
		Vec3f origin = new Vec3f(0,0,0);
		Vec3f[] array = new Vec3f[1];
		array[0] = origin;
		
		PointVAO3D buffer = new PointVAO3D();
		buffer.addData(array);
		
		Material material1 = new Material();
		material1.setDiffusemap(new Texture2D("./res/textures/sun/sun.png"));
		material1.getDiffusemap().bind();
		material1.getDiffusemap().trilinearFilter();
		
		Material material2 = new Material();
		material2.setDiffusemap(new Texture2D("./res/textures/sun/sun_small.png"));
		material2.getDiffusemap().bind();
		material2.getDiffusemap().trilinearFilter();
		
		setRenderInfo(new RenderInfo(new Default(),SunShader.getInstance()));
		Renderer renderer = new Renderer(SunShader.getInstance(), buffer);
		addComponent("Renderer", renderer);
		addComponent("Material1", material1);
		addComponent("Material2", material2);
		
		Light light = new Light();
		addComponent("Light", light);
		LightHandler.getLights().add(light);
	}
	
	public void render() {
		if (!RenderingEngine.isCameraUnderWater()){
			super.render();
		}
	}
}
