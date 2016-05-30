package modules.sky;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.ProceduralTexturing;
import engine.core.Texture;
import engine.modeling.Mesh;
import engine.modeling.obj.OBJLoader;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.MeshRenderer;
import engine.shaders.basic.Textured;

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
		MeshRenderer renderer = new MeshRenderer(meshBuffer, Textured.getInstance(), new CullFaceDisable());
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
