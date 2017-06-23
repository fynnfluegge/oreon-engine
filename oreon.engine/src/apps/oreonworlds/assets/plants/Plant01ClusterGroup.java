package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.GrassShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Plant01ClusterGroup extends InstancingObject{
	
	public Plant01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Plant_01","billboardmodel.obj","billboardmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.6f), GrassShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Plant01Cluster(20,new Vec3f(1692,0,-1254),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1406,0,-1181),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(1114,0,-1132),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1189,0,-1138),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(1086,0,-423),getObjectData()));
//		addChild(new Plant01Cluster(40,new Vec3f(1508,0,1400),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(782,0,765),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(959,0,-249),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(1054,0,-177),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(1039,0,372),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(571,0,1119),getObjectData()));
		addChild(new Plant01Cluster(20,new Vec3f(561,0,1406),getObjectData()));
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
