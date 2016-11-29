package samples.objLoader;

import engine.buffers.MeshVAO;
import engine.configs.CCW;
import engine.core.Util;
import engine.geometry.Geometrics;
import engine.geometry.Mesh;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.textures.Texture;

public class ActionBoxModel extends GameObject{
	
	public ActionBoxModel(){
		
		getTransform().setLocalScaling(5000, 5000, 5000);
		Mesh mesh = Geometrics.Cube();
		Util.generateNormalsCCW(mesh.getVertices(), mesh.getIndices());
		Material material = new Material();
		material.setColor(new Vec3f(0,0,0));
		material.setDiffusemap(new Texture("./res/textures/materials/metal/black metal small holes/black metal small holes.jpg"));
		material.setNormalmap(new Texture("./res/textures/materials/metal/black metal small holes/black metal small holes_NRM.jpg"));
		material.setEmission(1f);
		material.setShininess(10);
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		material.getNormalmap().bind();
		material.getNormalmap().mipmap();
		MeshVAO meshBuffer = new MeshVAO();
		meshBuffer.addData(mesh);
		setRenderInfo(new RenderInfo(new CCW(), engine.shaders.blinnphong.TBN.getInstance()));
		Renderer renderer = new Renderer(engine.shaders.blinnphong.TBN.getInstance(), meshBuffer);
		addComponent("Material", material);
		addComponent("Renderer", renderer);	
	}
}
