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
		
		addChild(new Rock01Cluster(8,new Vec3f(-2206,0,2908),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-2149,0,2870),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-2176,0,2916),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(555,0,1231),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(1285,0,488),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(819,0,82),getObjectData()));
//		addChild(new Rock01Cluster(8,new Vec3f(1504,0,1494),getObjectData()));
//		addChild(new Rock01Cluster(8,new Vec3f(507,0,1325),getObjectData()));
//		addChild(new Rock01Cluster(5,new Vec3f(925,0,-1024),getObjectData()));
//		addChild(new Rock01Cluster(8,new Vec3f(778,0,737),getObjectData()));
//		addChild(new Rock01Cluster(5,new Vec3f(949,0,-971),getObjectData()));
//		addChild(new Rock01Cluster(5,new Vec3f(861,0,-1035),getObjectData()));
//		addChild(new Rock01Cluster(5,new Vec3f(797,0,-1048),getObjectData()));
//		addChild(new Rock01Cluster(8,new Vec3f(1183,0,-493),getObjectData()));
//		addChild(new Rock01Cluster(8,new Vec3f(1027,0,-1122),getObjectData()));
//		addChild(new Rock01Cluster(12,new Vec3f(1549,0,-1264),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(788,0,189),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(1180,0,-1131),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(1120,0,-449),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(1114,0,-738),getObjectData()));
//		addChild(new Rock01Cluster(10,new Vec3f(627,0,1016),getObjectData()));
//		addChild(new Rock01Cluster(6,new Vec3f(902,0,-844),getObjectData()));
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
