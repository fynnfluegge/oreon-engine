package modules.sky;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.geometry.Mesh;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.shader.basic.BasicTexturedShader;
import engine.textures.ProceduralTexturing;
import engine.textures.Texture2D;
import modules.modelLoader.obj.OBJLoader;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Mesh mesh = new OBJLoader().load("./res/models/obj/dome", "dome.obj", null)[0].getMesh();
		ProceduralTexturing.dome(mesh);
		Material material = new Material();
		material.setDiffusemap(new Texture2D("./res/textures/sky/SkyDome8.png"));
		material.getDiffusemap().bind();
		material.getDiffusemap().trilinearFilter();
		MeshVAO meshBuffer = new MeshVAO();
		meshBuffer.addData(mesh);
		setRenderInfo(new RenderInfo(new CullFaceDisable(),BasicTexturedShader.getInstance()));
		Renderer renderer = new Renderer(BasicTexturedShader.getInstance(), meshBuffer);
		addComponent("Material", material);
		addComponent("Renderer", renderer);
	}
	
	public void update()
	{	
		getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
		getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
		getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
	}
}
