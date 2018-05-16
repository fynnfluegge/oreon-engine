package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLPointVBO3D;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.query.GLOcclusionQuery;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.parameter.AlphaBlendingSrcAlpha;
import org.oreon.core.gl.wrapper.texture.Texture2DTrilinearFilter;
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
		
		Material<GLTexture> material1 = new Material<GLTexture>();
		material1.setDiffusemap(new Texture2DTrilinearFilter("textures/sun/sun.png"));
		
		Material<GLTexture> material2 = new Material<GLTexture>();
		material2.setDiffusemap(new Texture2DTrilinearFilter("textures/sun/sun_small1.png"));
		
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
		
		if (!EngineContext.getRenderState().isUnderwater() && !EngineContext.getRenderState().isWireframe()){
			super.render();
		}
	}
}
