package modules.sky;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.geometrics.Mesh;
import engine.geometrics.obj.OBJLoader;
import engine.main.RenderingEngine;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.shaders.basic.Textured;
import engine.textures.ProceduralTexturing;
import engine.textures.Texture;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Mesh mesh = new OBJLoader().load("dome")[0].getMesh();
		ProceduralTexturing.dome(mesh);
		Material material = new Material();
		material.setDiffusemap(new Texture("./res/textures/sky/SkyDome8.png"));
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		MeshVAO meshBuffer = new MeshVAO();
		meshBuffer.addData(mesh);
		setRenderInfo(new RenderInfo(new CullFaceDisable(),Textured.getInstance()));
		Renderer renderer = new Renderer(Textured.getInstance(), meshBuffer);
		addComponent("Material", material);
		addComponent("Renderer", renderer);
	}
	
	public void update()
	{	
		getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
		getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
		getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
	}
	
	public void render(){
		if (!RenderingEngine.isGrid())
			super.render();
	}
}
