package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.memory.GLMeshVBO;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.GLAssimpModelLoader;
import org.oreon.core.gl.wrapper.parameter.CullFaceDisable;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ProceduralTexturing;


public class Atmosphere extends Renderable{
	
	public Atmosphere()
	{
		getWorldTransform().setLocalScaling(Constants.ZFAR, Constants.ZFAR, Constants.ZFAR);
		getWorldTransform().setLocalTranslation(0,-100,0);

		Mesh mesh = GLAssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
		ProceduralTexturing.dome(mesh);
		GLMeshVBO meshBuffer = new GLMeshVBO();
		meshBuffer.addData(mesh);
		GLRenderInfo renderInfo = new GLRenderInfo(BaseContext.getConfig().isAtmosphericScatteringApproximation()
				? AtmosphereShader.getInstance() : AtmosphericScatteringShader.getInstance(),
				new CullFaceDisable(), meshBuffer);
		
		GLDirectionalLight sunLight = new GLDirectionalLight();
		
		addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.WIREFRAME_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.LIGHT, sunLight);
	}
	
	public void render() {
		
		if (BaseContext.getConfig().isRenderRefraction() && !BaseContext.getConfig().isRenderUnderwater()){
			return;
		}
		else {
			super.render();
		}
	}

}
