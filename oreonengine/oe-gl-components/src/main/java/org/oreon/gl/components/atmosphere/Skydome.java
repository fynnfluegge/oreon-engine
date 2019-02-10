package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.memory.GLMeshVBO;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.GLAssimpModelLoader;
import org.oreon.core.gl.wrapper.parameter.CullFaceDisable;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.ProceduralTexturing;

public class Skydome extends Renderable{
	
	public Skydome()
	{
		Mesh mesh = GLAssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
		ProceduralTexturing.dome(mesh);
		GLMeshVBO meshBuffer = new GLMeshVBO();
		meshBuffer.addData(mesh);
		GLRenderInfo renderInfo = new GLRenderInfo(AtmosphereShader.getInstance(),
												   new CullFaceDisable(),
												   meshBuffer);
		addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(NodeComponentType.WIREFRAME_RENDERINFO, renderInfo);
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
