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

public class Rock01ClusterGroup extends InstancingObject{
	
	public Rock01ClusterGroup(){

		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
	
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
		
		addChild(new Rock01Cluster(8,new Vec3f(1018,0,-67),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1303,0,-206),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1593,0,-287),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(980,0,517),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(1285,0,488),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(819,0,82),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1504,0,1494),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1385,0,1637),getObjectData()));
	}
}
