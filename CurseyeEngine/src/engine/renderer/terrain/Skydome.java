package engine.renderer.terrain;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.ProceduralTexturing;
import engine.core.Texture;
import engine.gameObject.GameObject;
import engine.gameObject.components.MeshRenderer;
import engine.gameObject.components.Model;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.obj.OBJLoader;
import engine.shaderprograms.basic.Textured;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Model model = new OBJLoader().load("dome")[0];
		for (int i=0; i<model.getMesh().getVertices().length; i++)
		{
			ProceduralTexturing.dome(model.getMesh());
		}
		
		Material material = new Material();
		
		material.setDiffusemap(new Texture("./res/textures/sky/SkyDome8.png"));
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		material.setColor(new Vec3f(1,1,1));
		model.setMaterial(material);
		MeshVAO meshBuffer = new MeshVAO();
		MeshRenderer renderer = new MeshRenderer(meshBuffer, Textured.getInstance(), new CullFaceDisable());
		meshBuffer.addData(model.getMesh());
		addComponent("Model", model);
		addComponent("Renderer", renderer);
	}
	
	public void update()
	{	
		getTransform().setRotation(getTransform().getLocalRotation().add(getParent().getTransform().getRotation()));
		getTransform().setTranslation(getTransform().getLocalTranslation().add(getParent().getTransform().getTranslation()));
		getTransform().setScaling(getTransform().getLocalScaling().mul(getParent().getTransform().getScaling()));
	}	
}
