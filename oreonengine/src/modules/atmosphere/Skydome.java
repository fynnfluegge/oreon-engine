package modules.atmosphere;

import engine.buffers.MeshVBO;
import engine.components.model.Mesh;
import engine.components.renderer.RenderInfo;
import engine.components.renderer.Renderer;
import engine.configs.CullFaceDisable;
import engine.core.RenderingEngine;
import engine.scene.GameObject;
import engine.textures.ProceduralTexturing;
import modules.modelLoader.obj.OBJLoader;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Mesh mesh = new OBJLoader().load("./res/models/obj/dome", "dome.obj", null)[0].getMesh();
		ProceduralTexturing.dome(mesh);
		MeshVBO meshBuffer = new MeshVBO();
		meshBuffer.addData(mesh);
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(new CullFaceDisable(),AtmosphereShader.getInstance()));
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
