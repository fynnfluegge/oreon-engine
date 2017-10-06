package org.oreon.modules.gl.atmosphere;

import org.oreon.core.gl.buffers.GLPointVBO3D;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.query.GLOcclusionQuery;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.light.Light;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Material;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.renderer.Renderer;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;

public class Sun extends GameObject{
	
	public Sun(){
		
		getWorldTransform().setLocalTranslation(GLDirectionalLight.getInstance().getDirection().mul(-2800));
		Vec3f origin = new Vec3f(0,0,0);
		Vec3f[] array = new Vec3f[1];
		array[0] = origin;
		
		GLPointVBO3D buffer = new GLPointVBO3D();
		buffer.addData(array);
		
		Material material1 = new Material();
		material1.setDiffusemap(new Texture2D("textures/sun/sun.png"));
		material1.getDiffusemap().bind();
		material1.getDiffusemap().trilinearFilter();
		
		Material material2 = new Material();
		material2.setDiffusemap(new Texture2D("textures/sun/sun_small.png"));
		material2.getDiffusemap().bind();
		material2.getDiffusemap().trilinearFilter();
		
		Renderer renderer = new Renderer(buffer);
		renderer.setRenderInfo(new RenderInfo(new Default(),SunShader.getInstance()));
		addComponent("Renderer", renderer);
		addComponent("Material1", material1);
		addComponent("Material2", material2);
		
		Light light = new Light();
		light.setOcclusionQuery(new GLOcclusionQuery());
		addComponent("Light", light);
		LightHandler.getLights().add(light);
	}
	
	public void render() {
		if (!CoreSystem.getInstance().getRenderingEngine().isCameraUnderWater() && !CoreSystem.getInstance().getRenderingEngine().isGrid()){
			super.render();
		}
	}
}
