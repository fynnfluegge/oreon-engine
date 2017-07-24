package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.BushShader;
import apps.oreonworlds.shaders.plants.BushShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Bush01ClusterGroup extends InstancingObject{

	public Bush01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Bush_01","Bush_01.obj","Bush_01.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), BushShader.getInstance(), BushShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Bush01Cluster(20,new Vec3f(-2082,0,2881),getObjectData()));
		addChild(new Bush01Cluster(40,new Vec3f(-2125,0,2891),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(-2188,0,2894),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1288,0,1627),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1319,0,1679),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1255,0,1581),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-2173,0,2752),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-2104,0,2722),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1434,0,2638),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1518,0,2686),getObjectData()));
		addChild(new Bush01Cluster(6,new Vec3f(-1529,0,2093),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1262,0,1674),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1325,0,1647),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1245,0,1281),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(-1233,0,1318),getObjectData()));
		addChild(new Bush01Cluster(6,new Vec3f(-1214,0,1395),getObjectData()));
		addChild(new Bush01Cluster(6,new Vec3f(-1166,0,1511),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1228,0,1349),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1238,0,1384),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1222,0,1417),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-837,0,1501),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-853,0,1559),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-889,0,1621),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1958,0,2705),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1944,0,2632),getObjectData()));
		addChild(new Bush01Cluster(8,new Vec3f(-1863,0,2630),getObjectData()));
		addChild(new Bush01Cluster(6,new Vec3f(-1437,0,1823),getObjectData()));
	}

	@Override
	public void run() {
	}
}
