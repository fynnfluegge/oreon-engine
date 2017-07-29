package modules.atmosphere;

import engine.buffers.PointVBO3D;
import engine.components.light.DirectionalLight;
import engine.components.light.Light;
import engine.components.light.LightHandler;
import engine.components.model.Material;
import engine.components.renderer.RenderInfo;
import engine.components.renderer.Renderer;
import engine.configs.Default;
import engine.core.RenderingEngine;
import engine.math.Vec3f;
import engine.scene.GameObject;
import engine.textures.Texture2D;

public class Sun extends GameObject{
	
	public Sun(){
		
		getTransform().setLocalTranslation(DirectionalLight.getInstance().getDirection().mul(-2800));
		Vec3f origin = new Vec3f(0,0,0);
		Vec3f[] array = new Vec3f[1];
		array[0] = origin;
		
		PointVBO3D buffer = new PointVBO3D();
		buffer.addData(array);
		
		Material material1 = new Material();
		material1.setDiffusemap(new Texture2D("./res/textures/sun/sun.png"));
		material1.getDiffusemap().bind();
		material1.getDiffusemap().trilinearFilter();
		
		Material material2 = new Material();
		material2.setDiffusemap(new Texture2D("./res/textures/sun/sun_small.png"));
		material2.getDiffusemap().bind();
		material2.getDiffusemap().trilinearFilter();
		
		Renderer renderer = new Renderer(buffer);
		renderer.setRenderInfo(new RenderInfo(new Default(),SunShader.getInstance()));
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
