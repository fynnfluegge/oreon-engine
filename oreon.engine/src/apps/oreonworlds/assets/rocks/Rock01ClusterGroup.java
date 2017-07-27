package apps.oreonworlds.assets.rocks;

import apps.oreonworlds.shaders.rocks.RockHighPolyShader;
import apps.oreonworlds.shaders.rocks.RockShadowShader;
import engine.buffers.MeshVBO;
import engine.components.model.Model;
import engine.components.renderer.RenderInfo;
import engine.configs.Default;
import engine.math.Vec3f;
import engine.utils.Util;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.OBJLoader;

public class Rock01ClusterGroup extends InstancingObject{
	
	public Rock01ClusterGroup(){

		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVBO meshBuffer = new MeshVBO();
			model.getMesh().setTangentSpace(true);
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new Default(),RockHighPolyShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new Default(), RockShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Rock01Cluster(8,new Vec3f(-2183,0,2895),getObjectData()));
		addChild(new Rock01Cluster(4,new Vec3f(-2042,0,2717),getObjectData()));
		addChild(new Rock01Cluster(6,new Vec3f(-1464,0,2617),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-1295,0,1605),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-1321,0,1662),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-909,0,1602),getObjectData()));
		addChild(new Rock01Cluster(4,new Vec3f(-1464,0,2512),getObjectData()));
	}

	public void run() {}
}
