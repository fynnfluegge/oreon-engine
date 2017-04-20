package apps.oreonworlds.assets.rocks;

import apps.oreonworlds.shaders.rocks.RockHighPolyShader;
import apps.oreonworlds.shaders.rocks.RockShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.Default;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import engine.utils.Util;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Rock02ClusterGroup extends InstancingObject{

	public Rock02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/rocks/Rock_02","rock02.obj","rock02.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(true);
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new Default(),RockHighPolyShader.getInstance(), RockShadowShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Rock02Cluster(10,new Vec3f(954,0,-30),getObjectData()));
		addChild(new Rock02Cluster(10,new Vec3f(1240,0,-292),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(1062,0,446),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(1123,0,494),getObjectData()));
	}
}
