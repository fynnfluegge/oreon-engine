package org.oreon.gl.components.atmosphere;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLMeshVBO;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.GLAssimpModelLoader;
import org.oreon.core.gl.wrapper.parameter.CullFaceDisable;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.NodeComponentKey;
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
		addComponent(NodeComponentKey.MAIN_RENDERINFO, renderInfo);
	}
	
	public void update()
	{	
		getWorldTransform().setRotation(getWorldTransform().getLocalRotation().add(getParent().getWorldTransform().getRotation()));
		getWorldTransform().setTranslation(getWorldTransform().getLocalTranslation().add(getParent().getWorldTransform().getTranslation()));
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling().mul(getParent().getWorldTransform().getScaling()));
	}
	
	public void render() {
		if (EngineContext.getConfig().isRefraction() && !EngineContext.getConfig().isUnderwater()){
			return;
		}
		if (EngineContext.getConfig().isWireframe()){
			return;
		}
		else {
			super.render();
		}
	}
}
