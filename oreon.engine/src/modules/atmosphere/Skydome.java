package modules.atmosphere;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.RenderingEngine;
import engine.geometry.Mesh;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.textures.ProceduralTexturing;
import modules.modelLoader.obj.OBJLoader;

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
//		if (RenderingEngine.isWaterRefraction() && !RenderingEngine.isCameraUnderWater()){
//			return;
//		}
//		else {
			super.render();
//		}
	}
}
