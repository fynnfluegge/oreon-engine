package org.oreon.modules.gl.atmosphere;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.gl.scene.GLRenderInfo;
import org.oreon.core.model.AssimpModelLoader;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.texture.ProceduralTexturing;

public class Skydome extends Renderable{
	
	public Skydome()
	{
		Mesh mesh = AssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
		ProceduralTexturing.dome(mesh);
		GLMeshVBO meshBuffer = new GLMeshVBO();
		meshBuffer.addData(mesh);
		GLRenderInfo renderInfo = new GLRenderInfo(AtmosphereShader.getInstance(),
												   new CullFaceDisable(),
												   meshBuffer);
		addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
	}
	
	public void update()
	{	
		getWorldTransform().setRotation(getWorldTransform().getLocalRotation().add(getParent().getWorldTransform().getRotation()));
		getWorldTransform().setTranslation(getWorldTransform().getLocalTranslation().add(getParent().getWorldTransform().getTranslation()));
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling().mul(getParent().getWorldTransform().getScaling()));
	}
	
	public void render() {
		if (CoreSystem.getInstance().getRenderEngine().isWaterRefraction() && !CoreSystem.getInstance().getRenderEngine().isCameraUnderWater()){
			return;
		}
		if (CoreSystem.getInstance().getRenderEngine().isWireframe()){
			return;
		}
		else {
			super.render();
		}
	}
}
