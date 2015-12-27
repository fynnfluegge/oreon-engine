package simulations.objLoader;

import engine.buffers.MeshVAO;
import engine.configs.CCW;
import engine.core.Geometrics;
import engine.core.Texture;
import engine.core.Util;
import engine.core.Vertex;
import engine.gameObject.GameObject;
import engine.gameObject.components.MeshRenderer;
import engine.gameObject.components.Model;
import engine.math.Vec3f;
import engine.models.data.Material;

public class ActionBoxModel extends GameObject{
	
	public ActionBoxModel(){
		
		getTransform().setLocalScaling(5000, 5000, 5000);
		Model model = new Model(Geometrics.Cube());
		for(Vertex vertex : model.getMesh().getVertices()){
			vertex.setTextureCoord(vertex.getTextureCoord().mul(1));
		}
		Util.generateNormalsCCW(model.getMesh().getVertices(), model.getMesh().getIndices());
		Material material = new Material();
		material.setColor(new Vec3f(1,1,1));
		material.setDiffusemap(new Texture("./res/textures/materials/metal/black metal small holes/black metal small holes.jpg"));
		material.setNormalmap(new Texture("./res/textures/materials/metal/black metal small holes/black metal small holes_NRM.jpg"));
		material.setEmission(0.2f);
		material.setShininess(10);
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		material.getNormalmap().bind();
		material.getNormalmap().mipmap();
		model.setMaterial(material);
		MeshVAO meshBuffer = new MeshVAO();
		MeshRenderer renderer = new MeshRenderer(meshBuffer,engine.shaderprograms.phong.Bumpy.getInstance(), new CCW());
		meshBuffer.addData(model.getMesh());
		addComponent("Model", model);
		addComponent("Renderer", renderer);	
	}
}
