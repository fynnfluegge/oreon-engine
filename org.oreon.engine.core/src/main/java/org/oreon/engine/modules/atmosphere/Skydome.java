package org.oreon.engine.modules.atmosphere;

import org.oreon.engine.engine.buffers.MeshVAO;
import org.oreon.engine.engine.configs.CullFaceDisable;
import org.oreon.engine.engine.core.RenderingEngine;
import org.oreon.engine.engine.geometry.Mesh;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.components.RenderInfo;
import org.oreon.engine.engine.scenegraph.components.Renderer;
import org.oreon.engine.engine.textures.ProceduralTexturing;
import org.oreon.engine.modules.modelLoader.obj.OBJLoader;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Mesh mesh = new OBJLoader().load("./res/models/obj/dome", "dome.obj", null)[0].getMesh();
		ProceduralTexturing.dome(mesh);
		MeshVAO meshBuffer = new MeshVAO();
		meshBuffer.addData(mesh);
		setRenderInfo(new RenderInfo(new CullFaceDisable(),AtmosphereShader.getInstance()));
		Renderer renderer = new Renderer(AtmosphereShader.getInstance(), meshBuffer);
		addComponent("Renderer", renderer);
	}
	
	public void update()
	{	
		getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
		getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
		getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
	}
	
	public void render() {
		if (RenderingEngine.isWaterRefraction() && !RenderingEngine.isCameraUnderWater()){
			return;
		}
		else {
			super.render();
		}
	}
}
