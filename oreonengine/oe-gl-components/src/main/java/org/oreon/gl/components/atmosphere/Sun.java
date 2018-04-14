package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLPointVBO3D;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.parameter.AlphaBlendingSrcAlpha;
import org.oreon.core.gl.query.GLOcclusionQuery;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.light.Light;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;

public class Sun extends Renderable{
	
	public Sun(){
		
		getWorldTransform().setLocalTranslation(GLDirectionalLight.getInstance().getDirection().mul(-2600));
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
		material2.setDiffusemap(new Texture2D("textures/sun/sun_small1.png"));
		material2.getDiffusemap().bind();
		material2.getDiffusemap().trilinearFilter();
		
		GLRenderInfo renderInfo = new GLRenderInfo(SunShader.getInstance(),
											   new AlphaBlendingSrcAlpha(),
											   buffer);
		addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.MATERIAL0, material1);
		addComponent(NodeComponentType.MATERIAL1, material2);
		
		Light light = new Light();
		light.setOcclusionQuery(new GLOcclusionQuery());
		addComponent(NodeComponentType.LIGHT, light);
		LightHandler.getLights().add(light);
	}
	
	public void render() {
		
		if (!EngineContext.getConfig().isUnderwater() && !EngineContext.getConfig().isWireframe()){
			super.render();
		}
	}
}
